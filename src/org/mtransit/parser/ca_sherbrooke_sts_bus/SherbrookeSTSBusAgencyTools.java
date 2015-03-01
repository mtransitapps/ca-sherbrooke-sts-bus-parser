package org.mtransit.parser.ca_sherbrooke_sts_bus;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MSpec;
import org.mtransit.parser.mt.data.MTrip;

// http://donnees.ville.sherbrooke.qc.ca/dataset/transpo
// http://donnees.ville.sherbrooke.qc.ca/storage/f/2015-02-03T20:44:37.634Z/gtfs-stsherbrooke-hiver2015.zip
public class SherbrookeSTSBusAgencyTools extends DefaultAgencyTools {

	public static final String ROUTE_TYPE_FILTER = "3"; // bus only

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-sherbrooke-sts-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new SherbrookeSTSBusAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@Override
	public void start(String[] args) {
		System.out.printf("Generating STS bus data...\n");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("Generating STS bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public boolean excludeRoute(GRoute gRoute) {
		if (ROUTE_TYPE_FILTER != null && !gRoute.route_type.equals(ROUTE_TYPE_FILTER)) {
			return true;
		}
		return super.excludeRoute(gRoute);
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	@Override
	public long getRouteId(GRoute gRoute) {
		if (StringUtils.isNumeric(gRoute.route_id)) {
			return Long.valueOf(gRoute.route_id);
		} else if (gRoute.route_id.equals("EXPR")) {
			return 9999l;
		}
		Matcher matcher = DIGITS.matcher(gRoute.route_id);
		matcher.find();
		int digits = Integer.parseInt(matcher.group());
		int routeId;
		routeId = 0;
		return routeId + digits;
	}

	@Override
	public String getRouteShortName(GRoute gRoute) {
		if (gRoute.route_short_name.startsWith("E")) {
			return "E";
		}
		Matcher matcher = DIGITS.matcher(gRoute.route_id);
		matcher.find();
		return matcher.group();
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.route_long_name;
		if (StringUtils.isEmpty(routeLongName)) {
			if (gRoute.route_id.equals("EXPR")) {
				routeLongName = "IGA Extra - King Ouest <-> Northrop-Frye";
			} else {
				Matcher matcher = DIGITS.matcher(gRoute.route_id);
				matcher.find();
				int digits = Integer.parseInt(matcher.group());
				switch (digits) {
				case 1:
					routeLongName = "Carrefour De L'Estrie <-> Bowen - Talbot";
					break;
				case 2:
					routeLongName = "Cégep <-> U. Bishop's / Oxford";
					break;
				case 3:
					routeLongName = "Carrefour De L'Estrie <-> 13° Av. - du 24-Juin";
					break;
				case 4:
					routeLongName = "Carrefour De L'Estrie <-> Chalumeau";
					break;
				case 5:
					routeLongName = "Cégep <-> 13° Av. - du 24-Juin";
					break;
				case 6:
					routeLongName = "U. Sherbrooke <-> De Lisieux - Lachine";
					break;
				case 7:
					routeLongName = "André / Hallée <-> CHUS - Fleurimont";
					break;
				case 8:
					routeLongName = "U. Sherbrooke <-> CHUS - Fleurimont";
					break;
				case 9:
					routeLongName = "U. Sherbrooke <-> Chardonnerets";
					break;
				case 11:
					routeLongName = "U. Bishop's <-> Plateau St-Joseph";
					break;
				case 12:
					routeLongName = "Carrefour De L'Estrie <-> Cégep";
					break;
				case 13:
					routeLongName = "U. Sherbrooke <-> Raby - Normand";
					break;
				case 14:
					routeLongName = "U. Sherbrooke <-> Cégep";
					break;
				case 15:
					routeLongName = "U. Sherbrooke <-> Parc Blanchard";
					break;
				case 16:
					routeLongName = "U. Sherbrooke <-> Ontario - Prospect";
					break;
				case 17:
					routeLongName = "Cégep <-> Place Dussault";
					break;
				case 18:
					routeLongName = "U. Sherbrooke <-> Bourassa - Frontière";
					break;
				case 19:
					routeLongName = "Cégep <-> De Lisieux - Brûlé";
					break;
				case 20:
					routeLongName = "Cégep <-> St-François - Boulogne (Taxi-Bus)";
					break;
				case 21:
					routeLongName = "Place Fleurimont <-> CHUS - Fleurimont (Taxi-Bus)";
					break;
				case 22:
					routeLongName = "Place Fleurimont / Galvin <-> CHUS - Fleurimont";
					break;
				case 24:
					routeLongName = "U. Sherbrooke <-> Lotbinière - North Hatkey";
					break;
				case 25:
					routeLongName = "13° Av. - du 24-Juin <-> Gîte Du Bel Âge / Champêtre / Coquelicots (Taxi-Bus)";
					break;
				case 26:
					routeLongName = "Carrefour De L'Estrie <-> Parc Industriel (Taxi-Bus)";
					break;
				case 27:
					routeLongName = "U. Sherbrooke <-> Val-Du-Lac";
					break;
				case 28:
					routeLongName = "U. Bishop's <-> Alexander-Galt / Beattie / Atto";
					break;
				case 29:
					routeLongName = "Dépôt <-> U. Sherbrooke";
					break;
				case 49:
					routeLongName = "Northrop-Frye <-> CHUS - Hôtel-Dieu";
					break;
				case 50:
					routeLongName = "Carrefour De L'Estrie <-> Val-Des-Arbres / Laliberté";
					break;
				case 51:
					routeLongName = "Cégep <-> Kruger";
					break;
				case 52:
					routeLongName = "Terrasses Rock Forest <-> Av. Du Parc";
					break;
				case 53:
					routeLongName = "U. Sherbrooke <-> Campus De La Santé";
					break;
				case 54:
					routeLongName = "Northrop-Frye <-> CHUS - Fleurimont";
					break;
				case 55:
					routeLongName = "Du Manoir <-> 13° Av. - Du 24-Juin";
					break;
				case 56:
					routeLongName = "U. Bishop's <-> Oxford - Provigo Lennoxville (Taxi-Bus)";
					break;
				case 57:
					routeLongName = "Carrefour De L'Estrie <-> 13° Av. - Du 24-Juin";
					break;
				}
			}
		}
		routeLongName = MSpec.SAINT.matcher(routeLongName).replaceAll(MSpec.SAINT_REPLACEMENT);
		routeLongName = STATION_DU.matcher(routeLongName).replaceAll(STATION_DU_REPLACEMENT);
		routeLongName = UNIVERSITE_DE_SHERBROOKE.matcher(routeLongName).replaceAll(UNIVERSITE_DE_SHERBROOKE_REPLACEMENT);
		routeLongName = UNIVERSITE_BISHOP.matcher(routeLongName).replaceAll(UNIVERSITE_BISHOP_REPLACEMENT);
		return MSpec.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR = "0A3D53";

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Override
	public String getRouteColor(GRoute gRoute) {
		if (gRoute.route_id.equals("EXPR")) {
			return "231F20";
		}
		Matcher matcher = DIGITS.matcher(gRoute.route_id);
		matcher.find();
		int digits = Integer.parseInt(matcher.group());
		switch (digits) {
		case 1:
			return "B3D234";
		case 2:
			return "9E015E";
		case 3:
			return "0089CF";
		case 4:
			return "A8A9AD";
		case 5:
			return "FDBC12";
		case 6:
			return "F289B7";
		case 7:
			return "EE1D23";
		case 8:
			return "3AB54A";
		case 9:
			return "A25B09";
		case 11:
			return "EBDB01";
		case 12:
			return "007E3D";
		case 13:
			return "00B1B0";
		case 14:
			return "BC1B8D";
		case 15:
			return "F7931D";
		case 16:
			return "6C6D70";
		case 17:
			return "09428E";
		case 18:
			return "ED028C";
		case 19:
			return "56CBF5";
		case 20:
			return "6C6D70";
		case 21:
			return "09428E";
		case 22:
			return "FDBC12";
		case 24:
			return "EE1D23";
		case 25:
			return "ED028C";
		case 26:
			return "9E015E";
		case 27:
			return "B3D234";
		case 28:
			return "F289B7";
		case 29:
			return "0089CF";
		case 49:
			return "7671B4";
		case 50:
			return "F7931D";
		case 51:
			return "3AB54A";
		case 52:
			return "00AEEF";
		case 53:
			return "A8A9AD";
		case 54:
			return "A25B09";
		case 55:
			return "007E3D";
		case 56:
			return "692C91";
		case 57:
			return "56CBF5";
		}
		return super.getRouteColor(gRoute);
	}

	@Override
	public void setTripHeadsign(MRoute route, MTrip mTrip, GTrip gTrip) {
		String stationName = cleanTripHeadsign(gTrip.trip_headsign);
		int directionId = Integer.valueOf(gTrip.direction_id);
		if (route.id == 1l) {
			if (directionId == 0) {
				stationName = "Bowen - Talbot";
			} else {
				stationName = "Carrefour De L'Estrie";
			}
		} else if (route.id == 2l) {
			if (directionId == 0) {
				stationName = "Univ. Bishop's / Oxford";
			} else {
				stationName = "Cégep";
			}
		} else if (route.id == 3l) {
			if (directionId == 0) {
				stationName = "13° Av. - Du 24-Juin";
			} else {
				stationName = "Carrefour De L'Estrie";
			}
		} else if (route.id == 4l) {
			if (directionId == 0) {
				stationName = "Chalumeau";
			} else {
				stationName = "Carrefour De L'Estrie";
			}
		} else if (route.id == 5l) {
			if (directionId == 0) {
				stationName = "13° Av. / 24-Juin";
			} else {
				stationName = "Cégep";
			}
		} else if (route.id == 6l) {
			if (directionId == 0) {
				stationName = "Campus";
			} else {
				stationName = "Lisieux / Lachine";
			}
		} else if (route.id == 7l) {
			if (directionId == 0) {
				stationName = "CHUS / Fleurimont";
			} else {
				stationName = "André - Hallée";
			}
		} else if (route.id == 8l) {
			if (directionId == 0) {
				stationName = "CHUS / Fleurimont";
			} else {
				stationName = "Campus";
			}
		} else if (route.id == 9l) {
			if (directionId == 0) {
				stationName = "Chardonnerets";
			} else {
				stationName = "Campus";
			}
		} else if (route.id == 11l) {
			if (directionId == 0) {
				stationName = "Plateau St-Joseph";
			} else {
				stationName = "Univ. Bishop's";
			}
		} else if (route.id == 12l) {
			if (directionId == 0) {
				stationName = "Cégep";
			} else {
				stationName = "Carrefour De L'Estrie";
			}
		} else if (route.id == 13l) {
			if (directionId == 0) {
				stationName = "Raby / Normand";
			} else {
				stationName = "Campus";
			}
		} else if (route.id == 14l) {
			if (directionId == 0) {
				stationName = "Cégep";
			} else {
				stationName = "Campus";
			}
		} else if (route.id == 15l) {
			if (directionId == 0) {
				stationName = "Parc Blanchard";
			} else {
				stationName = "Campus";
			}
		} else if (route.id == 16l) {
			if (directionId == 0) {
				stationName = "Ontario / Prospect";
			} else {
				stationName = "Campus";
			}
		} else if (route.id == 17l) {
			if (directionId == 0) {
				stationName = "Cégep";
			} else {
				stationName = "Place Dussault";
			}
		} else if (route.id == 18l) {
			if (directionId == 0) {
				stationName = "Campus";
			} else {
				stationName = "Bourassa / Frontière";
			}
		} else if (route.id == 19l) {
			if (directionId == 0) {
				stationName = "Cégep";
			} else {
				stationName = "Lisieux / Brûlé";
			}
		} else if (route.id == 20l) {
			if (directionId == 0) {
				stationName = "St-François / Boulogne";
			} else {
				stationName = "Cégep";
			}
		} else if (route.id == 21l) {
			if (directionId == 0) {
				stationName = "CHUS";
			} else {
				stationName = "Place Fleurimont";
			}
		} else if (route.id == 22l) {
			if (directionId == 0) {
				stationName = "CHUS";
			} else {
				stationName = "Place Fleurimont";
			}
		} else if (route.id == 24l) {
			if (directionId == 0) {
				stationName = "Campus";
			} else {
				stationName = "Lotbinière / North Hatley";
			}
		} else if (route.id == 25l) {
			if (directionId == 0) {
				stationName = "13° Av. / 24-Juin";
			} else {
				stationName = "Gite Du Bel Âge / Champêtre / Coquelicots";
			}
		} else if (route.id == 26l) {
			if (directionId == 0) {
				stationName = "Parc Industriel";
			} else {
				stationName = "Carrefour De L''Estrie";
			}
		} else if (route.id == 27l) {
			if (directionId == 0) {
				stationName = "Campus";
			} else {
				stationName = "Val-Du-Lac";
			}
		} else if (route.id == 28l) {
			if (directionId == 0) {
				stationName = "Alexander-Galt / Beattle / Atto";
			} else {
				stationName = "Univ. Bishop's";
			}
		} else if (route.id == 49l) {
			if (directionId == 0) {
				stationName = "CHUS Hôtel-Dieu";
			} else {
				stationName = "Northrop-Frye";
			}
		} else if (route.id == 50l) {
			if (directionId == 0) {
				stationName = "Carrefour De L'Estrie";
			} else {
				stationName = "Val-Des-Arbres / Laliberté";
			}
		} else if (route.id == 51l) {
			if (directionId == 0) {
				stationName = "Kruger";
			} else {
				stationName = "Cégep";
			}
		} else if (route.id == 52l) {
			if (directionId == 0) {
				stationName = "Terrasses Rock-Forest";
			} else {
				stationName = "Av. Du Parc";
			}
		} else if (route.id == 53l) {
			if (directionId == 0) {
				stationName = "CHUS / CHUSFL (Porte 35)";
			} else {
				stationName = "Campus";
			}
		} else if (route.id == 54l) {
			if (directionId == 0) {
				stationName = "CHUS / Fleurimont";
			} else {
				stationName = "Northrop-Frye";
			}
		} else if (route.id == 55l) {
			if (directionId == 0) {
				stationName = "13° Av. / 24-Juin";
			} else {
				stationName = "Du Manoir";
			}
		} else if (route.id == 57l) {
			if (directionId == 0) {
				stationName = "13° Av. / 24-Juin";
			} else {
				stationName = "Carrefour De L'Estrie";
			}
		} else if (route.id == 9999l) {
			if (directionId == 0) {
				stationName = "Northrop-Frye";
			} else {
				stationName = "IGA Extra / King Ouest";
			}
		}
		mTrip.setHeadsignString(stationName, directionId);
	}

	private static final Pattern STATION_DU = Pattern.compile("(station du )", Pattern.CASE_INSENSITIVE);
	private static final String STATION_DU_REPLACEMENT = "";

	private static final Pattern UNIVERSITE_DE_SHERBROOKE = Pattern.compile("(université De Sherbrooke )", Pattern.CASE_INSENSITIVE);
	private static final String UNIVERSITE_DE_SHERBROOKE_REPLACEMENT = "U. Sherbrooke ";
	private static final Pattern UNIVERSITE_BISHOP = Pattern.compile("(université bishop's )", Pattern.CASE_INSENSITIVE);
	private static final String UNIVERSITE_BISHOP_REPLACEMENT = "U. Bishop's ";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = STATION_DU.matcher(tripHeadsign).replaceAll(STATION_DU_REPLACEMENT);
		tripHeadsign = UNIVERSITE_DE_SHERBROOKE.matcher(tripHeadsign).replaceAll(UNIVERSITE_DE_SHERBROOKE_REPLACEMENT);
		tripHeadsign = UNIVERSITE_BISHOP.matcher(tripHeadsign).replaceAll(UNIVERSITE_BISHOP_REPLACEMENT);
		tripHeadsign = AVENUE.matcher(tripHeadsign).replaceAll(AVENUE_REPLACEMENT);
		return MSpec.cleanLabelFR(tripHeadsign);
	}

	private static final Pattern AVENUE = Pattern.compile("( avenue)", Pattern.CASE_INSENSITIVE);
	private static final String AVENUE_REPLACEMENT = " av.";

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = STATION_DU.matcher(gStopName).replaceAll(STATION_DU_REPLACEMENT);
		gStopName = UNIVERSITE_DE_SHERBROOKE.matcher(gStopName).replaceAll(UNIVERSITE_DE_SHERBROOKE_REPLACEMENT);
		gStopName = UNIVERSITE_BISHOP.matcher(gStopName).replaceAll(UNIVERSITE_BISHOP_REPLACEMENT);
		gStopName = AVENUE.matcher(gStopName).replaceAll(AVENUE_REPLACEMENT);
		return super.cleanStopNameFR(gStopName);
	}

	@Override
	public String getStopCode(GStop gStop) {
		return super.getStopCode(gStop);
	}

	@Override
	public int getStopId(GStop gStop) {
		if (StringUtils.isNumeric(gStop.stop_id)) {
			return Integer.parseInt(gStop.stop_id);
		}
		Matcher matcher = DIGITS.matcher(gStop.stop_id);
		matcher.find();
		int digits = Integer.parseInt(matcher.group());
		int stopId = 0;
		if (gStop.stop_id.endsWith("A")) {
			stopId += 10000;
		} else if (gStop.stop_id.endsWith("B")) {
			stopId += 20000;
		} else if (gStop.stop_id.endsWith("C")) {
			stopId += 30000;
		} else if (gStop.stop_id.endsWith("D")) {
			stopId += 40000;
		} else if (gStop.stop_id.endsWith("E")) {
			stopId += 50000;
		} else if (gStop.stop_id.endsWith("F")) {
			stopId += 60000;
		} else if (gStop.stop_id.endsWith("G")) {
			stopId += 70000;
		} else if (gStop.stop_id.endsWith("H")) {
			stopId += 80000;
		} else if (gStop.stop_id.endsWith("I")) {
			stopId += 90000;
		} else if (gStop.stop_id.endsWith("J")) {
			stopId += 100000;
		} else if (gStop.stop_id.endsWith("K")) {
			stopId += 110000;
		} else {
			System.out.println("Stop doesn't have an ID (end with)! " + gStop);
			System.exit(-1);
		}
		return stopId + digits;
	}
}
