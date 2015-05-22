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
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MSpec;
import org.mtransit.parser.mt.data.MTrip;

// http://donnees.ville.sherbrooke.qc.ca/dataset/transpo
// http://donnees.ville.sherbrooke.qc.ca/storage/f/2015-02-03T20:44:37.634Z/gtfs-stsherbrooke-hiver2015.zip
public class SherbrookeSTSBusAgencyTools extends DefaultAgencyTools {

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
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	private static final String GRID_EXPR = "EXPR";
	private static final long RID_EXPR = 9999l;

	@Override
	public long getRouteId(GRoute gRoute) {
		if (StringUtils.isNumeric(gRoute.route_id)) {
			return Long.valueOf(gRoute.route_id);
		} else if (GRID_EXPR.equals(gRoute.route_id)) {
			return RID_EXPR;
		}
		Matcher matcher = DIGITS.matcher(gRoute.route_id);
		matcher.find();
		return Integer.parseInt(matcher.group());
	}

	private static final String RTS_EXPRESS = "E";
	private static final String RTS_EXPRESS_START_WITH = "E";

	@Override
	public String getRouteShortName(GRoute gRoute) {
		if (gRoute.route_short_name.startsWith(RTS_EXPRESS_START_WITH)) {
			return RTS_EXPRESS;
		}
		Matcher matcher = DIGITS.matcher(gRoute.route_id);
		matcher.find();
		return matcher.group();
	}

	private static final String IGA_EXTRA_KING_OUEST_NORTHROP_FRYE = "IGA Extra - King Ouest <-> Northrop-Frye";
	private static final String CARREFOUR_DE_L_ESTRIE_BOWEN_TALBOT = "Carrefour De L'Estrie <-> Bowen - Talbot";
	private static final String CÉGEP_U_BISHOP_S_OXFORD = "Cégep <-> U. Bishop's / Oxford";
	private static final String CARREFOUR_DE_L_ESTRIE_13_AV_DU_24_JUIN2 = "Carrefour De L'Estrie <-> 13° Av. - 24-Juin";
	private static final String CARREFOUR_DE_L_ESTRIE_CHALUMEAU = "Carrefour De L'Estrie <-> Chalumeau";
	private static final String CÉGEP_13_AV_DU_24_JUIN = "Cégep <-> 13° Av. - 24-Juin";
	private static final String U_SHERBROOKE_DE_LISIEUX_LACHINE = "U. Sherbrooke <-> De Lisieux - Lachine";
	private static final String ANDRÉ_HALLÉE_CHUS_FLEURIMONT = "André / Hallée <-> CHUS - Fleurimont";
	private static final String U_SHERBROOKE_CHUS_FLEURIMONT = "U. Sherbrooke <-> CHUS - Fleurimont";
	private static final String U_SHERBROOKE_CHARDONNERETS = "U. Sherbrooke <-> Chardonnerets";
	private static final String U_BISHOP_S_PLATEAU_ST_JOSEPH = "U. Bishop's <-> Plateau St-Joseph";
	private static final String CARREFOUR_DE_L_ESTRIE_CÉGEP = "Carrefour De L'Estrie <-> Cégep";
	private static final String U_SHERBROOKE_RABY_NORMAND = "U. Sherbrooke <-> Raby - Normand";
	private static final String U_SHERBROOKE_CÉGEP = "U. Sherbrooke <-> Cégep";
	private static final String U_SHERBROOKE_PARC_BLANCHARD = "U. Sherbrooke <-> Parc Blanchard";
	private static final String U_SHERBROOKE_ONTARIO_PROSPECT = "U. Sherbrooke <-> Ontario - Prospect";
	private static final String CÉGEP_PLACE_DUSSAULT = "Cégep <-> Place Dussault";
	private static final String U_SHERBROOKE_BOURASSA_FRONTIÈRE = "U. Sherbrooke <-> Bourassa - Frontière";
	private static final String CÉGEP_DE_LISIEUX_BRÛLÉ = "Cégep <-> De Lisieux - Brûlé";
	private static final String CÉGEP_ST_FRANÇOIS_BOULOGNE_TAXI_BUS = "Cégep <-> St-François - Boulogne (Taxi-Bus)";
	private static final String PLACE_FLEURIMONT_CHUS_FLEURIMONT_TAXI_BUS = "Place Fleurimont <-> CHUS - Fleurimont (Taxi-Bus)";
	private static final String PLACE_FLEURIMONT_GALVIN_CHUS_FLEURIMONT = "Place Fleurimont / Galvin <-> CHUS - Fleurimont";
	private static final String U_SHERBROOKE_LOTBINIÈRE_NORTH_HATKEY = "U. Sherbrooke <-> Lotbinière - North Hatkey";
	private static final String _13_AV_DU_24_JUIN_GÎTE_DU_BEL_ÂGE_CHAMPÊTRE_COQUELICOTS_TAXI_BUS = "13° Av. - 24-Juin <-> Gîte Du Bel Âge / Champêtre / Coquelicots (Taxi-Bus)";
	private static final String CARREFOUR_DE_L_ESTRIE_PARC_INDUSTRIEL_TAXI_BUS = "Carrefour De L'Estrie <-> Parc Industriel (Taxi-Bus)";
	private static final String U_SHERBROOKE_VAL_DU_LAC = "U. Sherbrooke <-> Val-Du-Lac";
	private static final String U_BISHOP_S_ALEXANDER_GALT_BEATTIE_ATTO = "U. Bishop's <-> Alexander-Galt / Beattie / Atto";
	private static final String DÉPÔT_U_SHERBROOKE = "Dépôt <-> U. Sherbrooke";
	private static final String NORTHROP_FRYE_CHUS_HÔTEL_DIEU = "Northrop-Frye <-> CHUS - Hôtel-Dieu";
	private static final String CARREFOUR_DE_L_ESTRIE_VAL_DES_ARBRES_LALIBERTÉ = "Carrefour De L'Estrie <-> Val-Des-Arbres / Laliberté";
	private static final String CÉGEP_KRUGER = "Cégep <-> Kruger";
	private static final String TERRASSES_ROCK_FOREST_AV_DU_PARC = "Terrasses Rock Forest <-> Av. Du Parc";
	private static final String U_SHERBROOKE_CAMPUS_DE_LA_SANTÉ = "U. Sherbrooke <-> Campus De La Santé";
	private static final String NORTHROP_FRYE_CHUS_FLEURIMONT = "Northrop-Frye <-> CHUS - Fleurimont";
	private static final String DU_MANOIR_13_AV_DU_24_JUIN = "Du Manoir <-> 13° Av. - 24-Juin";
	private static final String U_BISHOP_S_OXFORD_PROVIGO_LENNOXVILLE_TAXI_BUS = "U. Bishop's <-> Oxford - Provigo Lennoxville (Taxi-Bus)";
	private static final String CARREFOUR_DE_L_ESTRIE_13_AV_DU_24_JUIN = "Carrefour De L'Estrie <-> 13° Av. - 24-Juin";

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.route_long_name;
		if (StringUtils.isEmpty(routeLongName)) {
			if (GRID_EXPR.equals(gRoute.route_id)) {
				routeLongName = IGA_EXTRA_KING_OUEST_NORTHROP_FRYE;
			} else {
				Matcher matcher = DIGITS.matcher(gRoute.route_id);
				matcher.find();
				int digits = Integer.parseInt(matcher.group());
				switch (digits) {
				// @formatter:off
				case 1: routeLongName = CARREFOUR_DE_L_ESTRIE_BOWEN_TALBOT; break;
				case 2: routeLongName = CÉGEP_U_BISHOP_S_OXFORD; break;
				case 3: routeLongName = CARREFOUR_DE_L_ESTRIE_13_AV_DU_24_JUIN2; break;
				case 4: routeLongName = CARREFOUR_DE_L_ESTRIE_CHALUMEAU; break;
				case 5: routeLongName = CÉGEP_13_AV_DU_24_JUIN; break;
				case 6: routeLongName = U_SHERBROOKE_DE_LISIEUX_LACHINE; break;
				case 7: routeLongName = ANDRÉ_HALLÉE_CHUS_FLEURIMONT; break;
				case 8: routeLongName = U_SHERBROOKE_CHUS_FLEURIMONT; break;
				case 9: routeLongName = U_SHERBROOKE_CHARDONNERETS; break;
				case 11: routeLongName = U_BISHOP_S_PLATEAU_ST_JOSEPH; break;
				case 12: routeLongName = CARREFOUR_DE_L_ESTRIE_CÉGEP; break;
				case 13: routeLongName = U_SHERBROOKE_RABY_NORMAND; break;
				case 14: routeLongName = U_SHERBROOKE_CÉGEP; break;
				case 15: routeLongName = U_SHERBROOKE_PARC_BLANCHARD; break;
				case 16: routeLongName = U_SHERBROOKE_ONTARIO_PROSPECT; break;
				case 17: routeLongName = CÉGEP_PLACE_DUSSAULT; break;
				case 18: routeLongName = U_SHERBROOKE_BOURASSA_FRONTIÈRE; break;
				case 19: routeLongName = CÉGEP_DE_LISIEUX_BRÛLÉ; break;
				case 20: routeLongName = CÉGEP_ST_FRANÇOIS_BOULOGNE_TAXI_BUS; break;
				case 21: routeLongName = PLACE_FLEURIMONT_CHUS_FLEURIMONT_TAXI_BUS; break;
				case 22: routeLongName = PLACE_FLEURIMONT_GALVIN_CHUS_FLEURIMONT; break;
				case 24: routeLongName = U_SHERBROOKE_LOTBINIÈRE_NORTH_HATKEY; break;
				case 25: routeLongName = _13_AV_DU_24_JUIN_GÎTE_DU_BEL_ÂGE_CHAMPÊTRE_COQUELICOTS_TAXI_BUS; break;
				case 26: routeLongName = CARREFOUR_DE_L_ESTRIE_PARC_INDUSTRIEL_TAXI_BUS; break;
				case 27: routeLongName = U_SHERBROOKE_VAL_DU_LAC; break;
				case 28: routeLongName = U_BISHOP_S_ALEXANDER_GALT_BEATTIE_ATTO; break;
				case 29: routeLongName = DÉPÔT_U_SHERBROOKE; break;
				case 49: routeLongName = NORTHROP_FRYE_CHUS_HÔTEL_DIEU; break;
				case 50: routeLongName = CARREFOUR_DE_L_ESTRIE_VAL_DES_ARBRES_LALIBERTÉ; break;
				case 51: routeLongName = CÉGEP_KRUGER; break;
				case 52: routeLongName = TERRASSES_ROCK_FOREST_AV_DU_PARC; break;
				case 53: routeLongName = U_SHERBROOKE_CAMPUS_DE_LA_SANTÉ; break;
				case 54: routeLongName = NORTHROP_FRYE_CHUS_FLEURIMONT; break;
				case 55: routeLongName = DU_MANOIR_13_AV_DU_24_JUIN; break;
				case 56: routeLongName = U_BISHOP_S_OXFORD_PROVIGO_LENNOXVILLE_TAXI_BUS; break;
				case 57: routeLongName = CARREFOUR_DE_L_ESTRIE_13_AV_DU_24_JUIN; break;
				// @formatter:on
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

	private static final String COLOR_EBDB01 = "EBDB01";
	private static final String COLOR_00B1B0 = "00B1B0";
	private static final String COLOR_BC1B8D = "BC1B8D";
	private static final String COLOR_6C6D70 = "6C6D70";
	private static final String COLOR_09428E = "09428E";
	private static final String COLOR_FDBC12 = "FDBC12";
	private static final String COLOR_EE1D23 = "EE1D23";
	private static final String COLOR_ED028C = "ED028C";
	private static final String COLOR_9E015E = "9E015E";
	private static final String COLOR_B3D234 = "B3D234";
	private static final String COLOR_F289B7 = "F289B7";
	private static final String COLOR_0089CF = "0089CF";
	private static final String COLOR_7671B4 = "7671B4";
	private static final String COLOR_F7931D = "F7931D";
	private static final String COLOR_3AB54A = "3AB54A";
	private static final String COLOR_00AEEF = "00AEEF";
	private static final String COLOR_A8A9AD = "A8A9AD";
	private static final String COLOR_A25B09 = "A25B09";
	private static final String COLOR_007E3D = "007E3D";
	private static final String COLOR_692C91 = "692C91";
	private static final String COLOR_56CBF5 = "56CBF5";
	private static final String COLOR_231F20 = "231F20";

	@Override
	public String getRouteColor(GRoute gRoute) {
		if (GRID_EXPR.equals(gRoute.route_id)) {
			return COLOR_231F20;
		}
		Matcher matcher = DIGITS.matcher(gRoute.route_id);
		matcher.find();
		int digits = Integer.parseInt(matcher.group());
		switch (digits) {
		// @formatter:off
		case 1: return COLOR_B3D234;
		case 2: return COLOR_9E015E;
		case 3: return COLOR_0089CF;
		case 4: return COLOR_A8A9AD;
		case 5: return COLOR_FDBC12;
		case 6: return COLOR_F289B7;
		case 7: return COLOR_EE1D23;
		case 8: return COLOR_3AB54A;
		case 9: return COLOR_A25B09;
		case 11: return COLOR_EBDB01;
		case 12: return COLOR_007E3D;
		case 13: return COLOR_00B1B0;
		case 14: return COLOR_BC1B8D;
		case 15: return COLOR_F7931D;
		case 16: return COLOR_6C6D70;
		case 17: return COLOR_09428E;
		case 18: return COLOR_ED028C;
		case 19: return COLOR_56CBF5;
		case 20: return COLOR_6C6D70;
		case 21: return COLOR_09428E;
		case 22: return COLOR_FDBC12;
		case 24: return COLOR_EE1D23;
		case 25: return COLOR_ED028C;
		case 26: return COLOR_9E015E;
		case 27: return COLOR_B3D234;
		case 28: return COLOR_F289B7;
		case 29: return COLOR_0089CF;
		case 49: return COLOR_7671B4;
		case 50: return COLOR_F7931D;
		case 51: return COLOR_3AB54A;
		case 52: return COLOR_00AEEF;
		case 53: return COLOR_A8A9AD;
		case 54: return COLOR_A25B09;
		case 55: return COLOR_007E3D;
		case 56: return COLOR_692C91;
		case 57: return COLOR_56CBF5;
		// @formatter:on
		default:
			System.out.println("Unexpected route color " + gRoute);
			System.exit(-1);
			return null;
		}
	}

	private static final String BOWEN_TALBOT = "Bowen - Talbot";
	private static final String U_BISHOP_S_OXFORD = "U. Bishop's / Oxford";
	private static final String LISIEUX_LACHINE = "Lisieux / Lachine";
	private static final String CHARDONNERETS = "Chardonnerets";
	private static final String PARC_BLANCHARD = "Parc Blanchard";
	private static final String RABY_NORMAND = "Raby / Normand";
	private static final String ONTARIO_PROSPECT = "Ontario / Prospect";
	private static final String PLACE_DUSSAULT = "Place Dussault";
	private static final String BOURASSA_FRONTIÈRE = "Bourassa / Frontière";
	private static final String LISIEUX_BRÛLÉ = "Lisieux / Brûlé";
	private static final String ST_FRANÇOIS_BOULOGNE = "St-François / Boulogne";
	private static final String CHUS = "CHUS";
	private static final String PLACE_FLEURIMONT = "Place Fleurimont";
	private static final String LOTBINIÈRE_NORTH_HATLEY = "Lotbinière / North Hatley";
	private static final String GITE_DU_BEL_ÂGE_CHAMPÊTRE_COQUELICOTS = "Gite Du Bel Âge / Champêtre / Coquelicots";
	private static final String PARC_INDUSTRIEL = "Parc Industriel";
	private static final String CARREFOUR_DE_L_ESTRIE2 = "Carrefour De L''Estrie";
	private static final String VAL_DU_LAC = "Val-Du-Lac";
	private static final String ALEXANDER_GALT_BEATTLE_ATTO = "Alexander-Galt / Beattle / Atto";
	private static final String U_BISHOP_S = "U. Bishop's";
	private static final String CHUS_HÔTEL_DIEU = "CHUS Hôtel-Dieu";
	private static final String VAL_DES_ARBRES_LALIBERTÉ = "Val-Des-Arbres / Laliberté";
	private static final String KRUGER = "Kruger";
	private static final String CÉGEP = "Cégep";
	private static final String TERRASSES_ROCK_FOREST = "Terrasses Rock-Forest";
	private static final String AVE_DU_PARC = "Av. Du Parc";
	private static final String CHUS_CHUSFL_PORTE_35 = "CHUS / CHUSFL (Porte 35)";
	private static final String CAMPUS = "Campus";
	private static final String CHUS_FLEURIMONT = "CHUS / Fleurimont";
	private static final String DU_MANOIR = "Du Manoir";
	private static final String _13_AVE_24_JUIN = "13° Av. / 24-Juin";
	private static final String CARREFOUR_DE_L_ESTRIE = "Carrefour De L'Estrie";
	private static final String NORTHROP_FRYE = "Northrop-Frye";
	private static final String IGA_EXTRA_KING_OUEST = "IGA Extra / King Ouest";
	private static final String PLATEAU_ST_JOSEPH = "Plateau St-Joseph";
	private static final String ANDRÉ_HALLÉE = "André - Hallée";

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		String stationName = cleanTripHeadsign(gTrip.trip_headsign);
		if (mRoute.id == 1l) {
			if (gTrip.direction_id == 0) {
				stationName = BOWEN_TALBOT;
			} else {
				stationName = CARREFOUR_DE_L_ESTRIE;
			}
		} else if (mRoute.id == 2l) {
			if (gTrip.direction_id == 0) {
				stationName = U_BISHOP_S_OXFORD;
			} else {
				stationName = CÉGEP;
			}
		} else if (mRoute.id == 3l) {
			if (gTrip.direction_id == 0) {
				stationName = _13_AVE_24_JUIN;
			} else {
				stationName = CARREFOUR_DE_L_ESTRIE;
			}
		} else if (mRoute.id == 4l) {
			if (gTrip.direction_id == 0) {
				stationName = "Chalumeau";
			} else {
				stationName = CARREFOUR_DE_L_ESTRIE;
			}
		} else if (mRoute.id == 5l) {
			if (gTrip.direction_id == 0) {
				stationName = _13_AVE_24_JUIN;
			} else {
				stationName = CÉGEP;
			}
		} else if (mRoute.id == 6l) {
			if (gTrip.direction_id == 0) {
				stationName = CAMPUS;
			} else {
				stationName = LISIEUX_LACHINE;
			}
		} else if (mRoute.id == 7l) {
			if (gTrip.direction_id == 0) {
				stationName = CHUS_FLEURIMONT;
			} else {
				stationName = ANDRÉ_HALLÉE;
			}
		} else if (mRoute.id == 8l) {
			if (gTrip.direction_id == 0) {
				stationName = CHUS_FLEURIMONT;
			} else {
				stationName = CAMPUS;
			}
		} else if (mRoute.id == 9l) {
			if (gTrip.direction_id == 0) {
				stationName = CHARDONNERETS;
			} else {
				stationName = CAMPUS;
			}
		} else if (mRoute.id == 11l) {
			if (gTrip.direction_id == 0) {
				stationName = PLATEAU_ST_JOSEPH;
			} else {
				stationName = U_BISHOP_S;
			}
		} else if (mRoute.id == 12l) {
			if (gTrip.direction_id == 0) {
				stationName = CÉGEP;
			} else {
				stationName = CARREFOUR_DE_L_ESTRIE;
			}
		} else if (mRoute.id == 13l) {
			if (gTrip.direction_id == 0) {
				stationName = RABY_NORMAND;
			} else {
				stationName = CAMPUS;
			}
		} else if (mRoute.id == 14l) {
			if (gTrip.direction_id == 0) {
				stationName = CÉGEP;
			} else {
				stationName = CAMPUS;
			}
		} else if (mRoute.id == 15l) {
			if (gTrip.direction_id == 0) {
				stationName = PARC_BLANCHARD;
			} else {
				stationName = CAMPUS;
			}
		} else if (mRoute.id == 16l) {
			if (gTrip.direction_id == 0) {
				stationName = ONTARIO_PROSPECT;
			} else {
				stationName = CAMPUS;
			}
		} else if (mRoute.id == 17l) {
			if (gTrip.direction_id == 0) {
				stationName = CÉGEP;
			} else {
				stationName = PLACE_DUSSAULT;
			}
		} else if (mRoute.id == 18l) {
			if (gTrip.direction_id == 0) {
				stationName = CAMPUS;
			} else {
				stationName = BOURASSA_FRONTIÈRE;
			}
		} else if (mRoute.id == 19l) {
			if (gTrip.direction_id == 0) {
				stationName = CÉGEP;
			} else {
				stationName = LISIEUX_BRÛLÉ;
			}
		} else if (mRoute.id == 20l) {
			if (gTrip.direction_id == 0) {
				stationName = ST_FRANÇOIS_BOULOGNE;
			} else {
				stationName = CÉGEP;
			}
		} else if (mRoute.id == 21l) {
			if (gTrip.direction_id == 0) {
				stationName = CHUS;
			} else {
				stationName = PLACE_FLEURIMONT;
			}
		} else if (mRoute.id == 22l) {
			if (gTrip.direction_id == 0) {
				stationName = CHUS;
			} else {
				stationName = PLACE_FLEURIMONT;
			}
		} else if (mRoute.id == 24l) {
			if (gTrip.direction_id == 0) {
				stationName = CAMPUS;
			} else {
				stationName = LOTBINIÈRE_NORTH_HATLEY;
			}
		} else if (mRoute.id == 25l) {
			if (gTrip.direction_id == 0) {
				stationName = _13_AVE_24_JUIN;
			} else {
				stationName = GITE_DU_BEL_ÂGE_CHAMPÊTRE_COQUELICOTS;
			}
		} else if (mRoute.id == 26l) {
			if (gTrip.direction_id == 0) {
				stationName = PARC_INDUSTRIEL;
			} else {
				stationName = CARREFOUR_DE_L_ESTRIE2;
			}
		} else if (mRoute.id == 27l) {
			if (gTrip.direction_id == 0) {
				stationName = CAMPUS;
			} else {
				stationName = VAL_DU_LAC;
			}
		} else if (mRoute.id == 28l) {
			if (gTrip.direction_id == 0) {
				stationName = ALEXANDER_GALT_BEATTLE_ATTO;
			} else {
				stationName = U_BISHOP_S;
			}
		} else if (mRoute.id == 49l) {
			if (gTrip.direction_id == 0) {
				stationName = CHUS_HÔTEL_DIEU;
			} else {
				stationName = NORTHROP_FRYE;
			}
		} else if (mRoute.id == 50l) {
			if (gTrip.direction_id == 0) {
				stationName = CARREFOUR_DE_L_ESTRIE;
			} else {
				stationName = VAL_DES_ARBRES_LALIBERTÉ;
			}
		} else if (mRoute.id == 51l) {
			if (gTrip.direction_id == 0) {
				stationName = KRUGER;
			} else {
				stationName = CÉGEP;
			}
		} else if (mRoute.id == 52l) {
			if (gTrip.direction_id == 0) {
				stationName = TERRASSES_ROCK_FOREST;
			} else {
				stationName = AVE_DU_PARC;
			}
		} else if (mRoute.id == 53l) {
			if (gTrip.direction_id == 0) {
				stationName = CHUS_CHUSFL_PORTE_35;
			} else {
				stationName = CAMPUS;
			}
		} else if (mRoute.id == 54l) {
			if (gTrip.direction_id == 0) {
				stationName = CHUS_FLEURIMONT;
			} else {
				stationName = NORTHROP_FRYE;
			}
		} else if (mRoute.id == 55l) {
			if (gTrip.direction_id == 0) {
				stationName = _13_AVE_24_JUIN;
			} else {
				stationName = DU_MANOIR;
			}
		} else if (mRoute.id == 57l) {
			if (gTrip.direction_id == 0) {
				stationName = _13_AVE_24_JUIN;
			} else {
				stationName = CARREFOUR_DE_L_ESTRIE;
			}
		} else if (mRoute.id == 9999l) {
			if (gTrip.direction_id == 0) {
				stationName = NORTHROP_FRYE;
			} else {
				stationName = IGA_EXTRA_KING_OUEST;
			}
		}
		mTrip.setHeadsignString(stationName, gTrip.direction_id);
	}

	private static final Pattern STATION_DU = Pattern.compile("(station du )", Pattern.CASE_INSENSITIVE);
	private static final String STATION_DU_REPLACEMENT = "";

	private static final Pattern UNIVERSITE_DE_SHERBROOKE = Pattern.compile("(université de sherbrooke)", Pattern.CASE_INSENSITIVE);
	private static final String UNIVERSITE_DE_SHERBROOKE_REPLACEMENT = "U. Sherbrooke";
	private static final Pattern UNIVERSITE_BISHOP = Pattern.compile("(université bishop's)", Pattern.CASE_INSENSITIVE);
	private static final String UNIVERSITE_BISHOP_REPLACEMENT = U_BISHOP_S;

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
