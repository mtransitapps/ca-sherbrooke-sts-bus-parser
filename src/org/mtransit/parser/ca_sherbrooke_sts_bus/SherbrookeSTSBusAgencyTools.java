package org.mtransit.parser.ca_sherbrooke_sts_bus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.parser.CleanUtils;
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
import org.mtransit.parser.mt.data.MTrip;

// https://www.donneesquebec.ca/recherche/fr/dataset/transport-sts
// CURRENT: https://www.donneesquebec.ca/recherche/dataset/e82b9141-09d8-4f85-af37-d84937bc2503/resource/b7f43b2a-2557-4e3b-ba12-5a5c6d4de5b1/download/gtfsstsherbrooke.zip
// NEXT: https://www.donneesquebec.ca/recherche/dataset/e82b9141-09d8-4f85-af37-d84937bc2503/resource/ee82a1e9-7405-47c6-ac5e-6c0ffda92ce4/download/gtfsclientsstshaut1920190729.zip
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
		System.out.printf("\nGenerating STS bus data...");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("\nGenerating STS bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludingAll() {
		return this.serviceIds != null && this.serviceIds.isEmpty();
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
	private static final long RID_EXPR = 9_999L;

	private static final String S = "S";
	private static final String X = "X";

	private static final long RID_ENDS_WITH_S = 19_000L;
	private static final long RID_ENDS_WITH_X = 24_000L;

	@Override
	public long getRouteId(GRoute gRoute) {
		if (StringUtils.isNumeric(gRoute.getRouteShortName())) {
			return Long.parseLong(gRoute.getRouteShortName());
		}
		if (GRID_EXPR.equals(gRoute.getRouteShortName())) {
			return RID_EXPR;
		}
		Matcher matcher = DIGITS.matcher(gRoute.getRouteShortName());
		if (matcher.find()) {
			long digits = Long.parseLong(matcher.group());
			if (gRoute.getRouteShortName().endsWith(S)) {
				return digits + RID_ENDS_WITH_S;
			} else if (gRoute.getRouteShortName().endsWith(X)) {
				return digits + RID_ENDS_WITH_X;
			}
		}
		System.out.printf("\nUnexpected route ID for %s!\n", gRoute);
		System.exit(-1);
		return -1l;
	}

	private static final String RTS_EXPRESS = "E";

	@Override
	public String getRouteShortName(GRoute gRoute) {
		if (GRID_EXPR.equals(gRoute.getRouteShortName())) {
			return RTS_EXPRESS;
		}
		return super.getRouteShortName(gRoute);
	}

	private static final String RLN_SPLIT = "-";

	// http://www.toponymie.gouv.qc.ca/ct/normes-procedures/terminologie-geographique/liste-termes-geographiques.html
	private static final String AVE = "Av.";
	private static final String CARREFOUR = "Carref.";
	private static final String PARC = "Parc";
	private static final String PLACE = "Place";
	private static final String PLATEAU_SHORT = "Pl.";
	private static final String PARC_INDUSTRIEL = PARC + " Ind.";
	private static final String TERRASSES = "Tsses";

	private static final String _SLASH_ = " / ";

	private static final String U_DE_S = "UdeS";
	private static final String U_BISHOP_S = "U Bishop's";
	private static final String MANOIR = "Manoir";
	private static final String DU = "Du";
	private static final String CHAMPÊTRE = "Champêtre";
	private static final String GITE_DU_BEL_ÂGE = "Gite Du Bel Âge";
	private static final String ALEXANDER_GALT = "Alexander-Galt";
	private static final String VAL_DES_ARBRES = "Val-Des-Arbres";
	private static final String ATTO = "Atto";
	private static final String COQUELICOTS = "Coquelicots";
	private static final String LALIBERTÉ = "Laliberté";
	private static final String ROCK_FOREST = "Rock-Forest";
	private static final String NORTH_HATLEY = "North Hatley";
	private static final String LOTBINIÈRE = "Lotbinière";
	private static final String BOULOGNE = "Boulogne";
	private static final String ST_FRANÇOIS = "St-François";
	private static final String FRONTIÈRE = "Frontière";
	private static final String BOURASSA = "Bourassa";
	private static final String DUSSAULT = "Dussault";
	private static final String ONTARIO = "Ontario";
	private static final String PROSPECT = "Prospect";
	private static final String BLANCHARD = "Blanchard";
	private static final String RABY = "Raby";
	private static final String LACHINE = "Lachine";
	private static final String HÔTEL_DIEU = "Hôtel-Dieu";
	private static final String BOWEN = "Bowen";
	private static final String TALBOT = "Talbot";
	private static final String KING_OUEST = "King Ouest";
	private static final String IGA_EXTRA = "IGA Extra";
	private static final String NORMAND = "Normand";
	private static final String ST_JOSEPH = "St-Joseph";
	private static final String BRÛLÉ = "Brûlé";
	private static final String ANDRÉ = "André";
	private static final String HABITAT = "Habitat";
	private static final String HALLÉE = "Hallée";
	private static final String DEPOT = "Dépôt";
	private static final String CARREFOUR_DE_L_ESTRIE = CARREFOUR + " De L'Estrie";
	private static final String NORTHROP_FRYE = "Northrop-Frye";
	private static final String CHALUMEAU = "Chalumeau";
	private static final String OXFORD = "Oxford";
	private static final String CROISSANT_OXFORD = "Croissant " + OXFORD;
	private static final String FLEURIMONT = "Fleurimont";
	private static final String CHUS = "CHUS";
	private static final String CHUS_FLEURIMONT = CHUS + " " + FLEURIMONT;
	private static final String CHUS_HÔTEL_DIEU = CHUS + " " + HÔTEL_DIEU;
	private static final String CHUS_URGENCE = CHUS + " (Urgence)";
	private static final String PLACE_FLEURIMONT = PLACE + " " + FLEURIMONT;
	private static final String LISIEUX = "Lisieux";
	private static final String KRUGER = "Kruger";
	private static final String CHARDONNERETS = "Chardonnerets";
	private static final String MARIKA = "Marika";
	private static final String PLATEAU_ST_JOSEPH = PLATEAU_SHORT + " " + ST_JOSEPH;
	private static final String CEGEP = "Cégep";
	private static final String VAL_DU_LAC = "Val-Du-Lac";
	private static final String PARC_BLANCHARD = PARC + " " + BLANCHARD;
	private static final String PLACE_DUSSAULT = PLACE + " " + DUSSAULT;
	private static final String TAXI_BUS = "(Taxi-Bus)";
	private static final String GALVIN = "Galvin";
	private static final String PROVIGO_LENNOXVILLE = "Provigo Lennoxville";
	private static final String CAMPUS = "Campus";
	private static final String BEATTLE = "Beattle";
	private static final String DU_MANOIR = DU + " " + MANOIR;
	private static final String AVE_DU_PARC = AVE + " " + DU + " " + PARC;
	private static final String TERRASSES_ROCK_FOREST = TERRASSES + " " + ROCK_FOREST;
	private static final String É_FONTAINE = "É.-Fontaine";
	private static final String ST_ROCH = "St-Roch";
	private static final String FRONTENAC = "Frontenac";
	private static final String BELVÉDÈRE = "Belvédère";

	private static final String ST_ROCH_É_FONTAINE = ST_ROCH + _SLASH_ + É_FONTAINE;
	private static final String CHARDONNERETS_MARIKA = CHARDONNERETS + _SLASH_ + MARIKA;
	private static final String PLACE_FLEURIMONT_GALVIN = PLACE_FLEURIMONT + _SLASH_ + GALVIN;
	private static final String LISIEUX_BRÛLÉ = LISIEUX + _SLASH_ + BRÛLÉ;
	private static final String LISIEUX_LACHINE = LISIEUX + _SLASH_ + LACHINE;
	private static final String ANDRÉ_HALLÉE = ANDRÉ + _SLASH_ + HALLÉE;
	private static final String HABITAT_ANDRE = HABITAT + _SLASH_ + ANDRÉ;
	private static final String RABY_NORMAND = RABY + _SLASH_ + NORMAND;
	private static final String ONTARIO_PROSPECT = ONTARIO + _SLASH_ + PROSPECT;
	private static final String FRONTENAC_BELVÉDÈRE = FRONTENAC + _SLASH_ + BELVÉDÈRE;
	private static final String IGA_EXTRA_KING_OUEST = IGA_EXTRA + _SLASH_ + KING_OUEST;
	private static final String BOWEN_TALBOT = BOWEN + _SLASH_ + TALBOT;
	private static final String BOURASSA_FRONTIÈRE = BOURASSA + _SLASH_ + FRONTIÈRE;
	private static final String ST_FRANÇOIS_BOULOGNE = ST_FRANÇOIS + _SLASH_ + BOULOGNE;
	private static final String _13_AVE_24_JUIN = "13e " + AVE + _SLASH_ + "24-Juin";
	private static final String CHALUMEAU_GALVIN = CHALUMEAU + _SLASH_ + GALVIN;
	private static final String LOTBINIÈRE_NORTH_HATLEY = LOTBINIÈRE + _SLASH_ + NORTH_HATLEY;
	private static final String U_BISHOP_S_OXFORD = U_BISHOP_S + _SLASH_ + OXFORD;
	private static final String GITE_DU_BEL_ÂGE_CHAMPÊTRE_COQUELICOTS = GITE_DU_BEL_ÂGE + _SLASH_ + CHAMPÊTRE + _SLASH_ + COQUELICOTS;
	private static final String ALEXANDER_GALT_BEATTLE_ATTO = ALEXANDER_GALT + _SLASH_ + BEATTLE + _SLASH_ + ATTO;
	private static final String VAL_DES_ARBRES_LALIBERTÉ = VAL_DES_ARBRES + _SLASH_ + LALIBERTÉ;

	private static final String IGA_EXTRA_KING_OUEST_NORTHROP_FRYE = IGA_EXTRA_KING_OUEST + " " + RLN_SPLIT + " " + NORTHROP_FRYE;
	private static final String RLN_1 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + BOWEN_TALBOT;
	private static final String RLN_2 = CEGEP + " " + RLN_SPLIT + " " + U_BISHOP_S_OXFORD;
	private static final String RLN_3 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + _13_AVE_24_JUIN;
	private static final String RLN_4 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + CHALUMEAU;
	private static final String RLN_5 = CEGEP + " " + RLN_SPLIT + " " + _13_AVE_24_JUIN;
	private static final String RLN_6 = U_DE_S + " " + RLN_SPLIT + " " + LISIEUX_LACHINE;
	private static final String RLN_7 = ANDRÉ_HALLÉE + " " + RLN_SPLIT + " " + CHUS_FLEURIMONT;
	private static final String RLN_8 = U_DE_S + " " + RLN_SPLIT + " " + CHUS_FLEURIMONT;
	private static final String RLN_9 = U_DE_S + " " + RLN_SPLIT + " " + CHARDONNERETS;
	private static final String RLN_11 = U_BISHOP_S + " " + RLN_SPLIT + " " + PLATEAU_ST_JOSEPH;
	private static final String RLN_12 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + CEGEP;
	private static final String RLN_13 = U_DE_S + " " + RLN_SPLIT + " " + RABY_NORMAND;
	private static final String RLN_14 = U_DE_S + " " + RLN_SPLIT + " " + CEGEP;
	private static final String RLN_15 = U_DE_S + " " + RLN_SPLIT + " " + PARC_BLANCHARD;
	private static final String RLN_16 = U_DE_S + " " + RLN_SPLIT + " " + ONTARIO_PROSPECT;
	private static final String RLN_17 = CEGEP + " " + RLN_SPLIT + " " + PLACE_DUSSAULT;
	private static final String RLN_18 = U_DE_S + " " + RLN_SPLIT + " " + BOURASSA_FRONTIÈRE;
	private static final String RLN_19 = CEGEP + " " + RLN_SPLIT + LISIEUX_BRÛLÉ;
	private static final String RLN_20 = CEGEP + " " + RLN_SPLIT + " " + ST_FRANÇOIS_BOULOGNE + " " + TAXI_BUS;
	private static final String RLN_21 = PLACE_FLEURIMONT + " " + RLN_SPLIT + " " + CHUS_FLEURIMONT + " " + TAXI_BUS;
	private static final String RLN_22 = PLACE_FLEURIMONT_GALVIN + " " + RLN_SPLIT + " " + CHUS_FLEURIMONT;
	private static final String RLN_24 = U_DE_S + " " + RLN_SPLIT + " " + LOTBINIÈRE_NORTH_HATLEY;
	private static final String RLN_25 = _13_AVE_24_JUIN + " " + RLN_SPLIT + " " + GITE_DU_BEL_ÂGE_CHAMPÊTRE_COQUELICOTS + " " + TAXI_BUS;
	private static final String RLN_26 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + PARC_INDUSTRIEL + " " + TAXI_BUS;
	private static final String RLN_27 = U_DE_S + " " + RLN_SPLIT + " " + VAL_DU_LAC;
	private static final String RLN_28 = U_BISHOP_S + " " + RLN_SPLIT + " " + ALEXANDER_GALT_BEATTLE_ATTO;
	private static final String RLN_29 = "Dépôt " + RLN_SPLIT + " " + U_DE_S;
	private static final String RLN_49 = NORTHROP_FRYE + " " + RLN_SPLIT + " " + CHUS_HÔTEL_DIEU;
	private static final String RLN_50 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + VAL_DES_ARBRES_LALIBERTÉ;
	private static final String RLN_51 = CEGEP + " " + RLN_SPLIT + " " + KRUGER;
	private static final String RLN_52 = TERRASSES_ROCK_FOREST + " " + RLN_SPLIT + " " + AVE_DU_PARC;
	private static final String RLN_53 = U_DE_S + " " + RLN_SPLIT + " " + CAMPUS + " De La Santé";
	private static final String RLN_54 = NORTHROP_FRYE + " " + RLN_SPLIT + " " + CHUS_FLEURIMONT;
	private static final String RLN_55 = DU_MANOIR + " " + RLN_SPLIT + " " + _13_AVE_24_JUIN;
	private static final String RLN_56 = U_BISHOP_S + " " + RLN_SPLIT + " " + OXFORD + _SLASH_ + PROVIGO_LENNOXVILLE + " " + TAXI_BUS;
	private static final String RLN_57 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + _13_AVE_24_JUIN;

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.getRouteLongName();
		if (StringUtils.isEmpty(routeLongName)) {
			if (GRID_EXPR.equals(gRoute.getRouteShortName())) {
				routeLongName = IGA_EXTRA_KING_OUEST_NORTHROP_FRYE;
			} else {
				Matcher matcher = DIGITS.matcher(gRoute.getRouteShortName());
				if (matcher.find()) {
					int digits = Integer.parseInt(matcher.group());
					switch (digits) {
					// @formatter:off
					case 1: routeLongName = RLN_1; break;
					case 2: routeLongName = RLN_2; break;
					case 3: routeLongName = RLN_3; break;
					case 4: routeLongName = RLN_4; break;
					case 5: routeLongName = RLN_5; break;
					case 6: routeLongName = RLN_6; break;
					case 7: routeLongName = RLN_7; break;
					case 8: routeLongName = RLN_8; break;
					case 9: routeLongName = RLN_9; break;
					case 11: routeLongName = RLN_11; break;
					case 12: routeLongName = RLN_12; break;
					case 13: routeLongName = RLN_13; break;
					case 14: routeLongName = RLN_14; break;
					case 15: routeLongName = RLN_15; break;
					case 16: routeLongName = RLN_16; break;
					case 17: routeLongName = RLN_17; break;
					case 18: routeLongName = RLN_18; break;
					case 19: routeLongName = RLN_19; break;
					case 20: routeLongName = RLN_20; break;
					case 21: routeLongName = RLN_21; break;
					case 22: routeLongName = RLN_22; break;
					case 24: routeLongName = RLN_24; break;
					case 25: routeLongName = RLN_25; break;
					case 26: routeLongName = RLN_26; break;
					case 27: routeLongName = RLN_27; break;
					case 28: routeLongName = RLN_28; break;
					case 29: routeLongName = RLN_29; break;
					case 49: routeLongName = RLN_49; break;
					case 50: routeLongName = RLN_50; break;
					case 51: routeLongName = RLN_51; break;
					case 52: routeLongName = RLN_52; break;
					case 53: routeLongName = RLN_53; break;
					case 54: routeLongName = RLN_54; break;
					case 55: routeLongName = RLN_55; break;
					case 56: routeLongName = RLN_56; break;
					case 57: routeLongName = RLN_57; break;
					// @formatter:on
					}
				}
			}
		}
		if (StringUtils.isEmpty(routeLongName)) {
			System.out.printf("\nUnexpected route long name for %s!\n", gRoute);
			System.exit(-1);
			return null;
		}
		routeLongName = CleanUtils.SAINT.matcher(routeLongName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		routeLongName = STATION_DU.matcher(routeLongName).replaceAll(STATION_DU_REPLACEMENT);
		routeLongName = UNIVERSITE_DE_SHERBROOKE.matcher(routeLongName).replaceAll(UNIVERSITE_DE_SHERBROOKE_REPLACEMENT);
		routeLongName = UNIVERSITE_BISHOP.matcher(routeLongName).replaceAll(UNIVERSITE_BISHOP_REPLACEMENT);
		routeLongName = CleanUtils.cleanStreetTypesFRCA(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR = "0A3D53";

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Override
	public String getRouteColor(GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteColor())) {
			if (GRID_EXPR.equals(gRoute.getRouteShortName())) {
				return "231F20";
			}
			try {
				Matcher matcher = DIGITS.matcher(gRoute.getRouteShortName());
				if (matcher.find()) {
					int digits = Integer.parseInt(matcher.group());
					switch (digits) {
					// @formatter:off
					case 1: return "B3D234";
					case 2: return "9E015E";
					case 3: return "0089CF";
					case 4: return "A8A9AD";
					case 5: return "FDBC12";
					case 6: return "F289B7";
					case 7: return "EE1D23";
					case 8: return "3AB54A";
					case 9: return "A25B09";
					case 11: return "E6E600";
					case 12: return "007E3D";
					case 13: return "00B1B0";
					case 14: return "BC1B8D";
					case 15: return "F7931D";
					case 16: return "6C6D70";
					case 17: return "09428E";
					case 18: return "ED028C";
					case 19: return "56CBF5";
					case 20: return "6C6D70";
					case 21: return "09428E";
					case 22: return "FDBC12";
					case 23: return "B31E7A";
					case 24: return "EE1D23";
					case 25: return "ED028C";
					case 26: return "9E015E";
					case 27: return "B3D234";
					case 28: return "F289B7";
					case 29: return "0089CF";
					case 49: return "7671B4";
					case 50: return "F7931D";
					case 51: return "3AB54A";
					case 52: return "00AEEF";
					case 53: return "A8A9AD";
					case 54: return "A25B09";
					case 55: return "007E3D";
					case 56: return "692C91";
					case 57: return "56CBF5";
					case 70: return "544CA9";
					// @formatter:on
					}
				}
				System.out.printf("\nUnexpected route color %s!\n", gRoute);
				System.exit(-1);
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.printf("\nUnexpected route color %s!\n", gRoute);
				System.exit(-1);
				return null;
			}
		}
		return super.getRouteColor(gRoute);
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), gTrip.getDirectionId());
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		List<String> headsignsValues = Arrays.asList(mTrip.getHeadsignValue(), mTripToMerge.getHeadsignValue());
		if (mTrip.getRouteId() == 1L) {
			if (Arrays.asList( //
					CEGEP, // <>
					DEPOT, //
					BOWEN_TALBOT //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(BOWEN_TALBOT, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					CEGEP, // <>
					CARREFOUR_DE_L_ESTRIE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CARREFOUR_DE_L_ESTRIE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 2L) {
			if (Arrays.asList( //
					DEPOT, //
					CEGEP //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CEGEP, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					"College" + _SLASH_ + "Mitchell", //
					CROISSANT_OXFORD, //
					U_BISHOP_S //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(U_BISHOP_S, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 3L) {
			if (Arrays.asList( //
					CEGEP, //
					DEPOT, //
					_13_AVE_24_JUIN //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(_13_AVE_24_JUIN, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					CEGEP, //
					DEPOT, //
					CARREFOUR_DE_L_ESTRIE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CARREFOUR_DE_L_ESTRIE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 3L + RID_ENDS_WITH_S) { // 3S
			if (Arrays.asList( //
					"Industriel" + _SLASH_ + "Letellier", //
					CEGEP //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CEGEP, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 4L) {
			if (Arrays.asList( //
					CEGEP, //
					CHALUMEAU_GALVIN //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHALUMEAU_GALVIN, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					CEGEP, //
					"Galt E." + _SLASH_ + "Conseil", //
					CARREFOUR_DE_L_ESTRIE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CARREFOUR_DE_L_ESTRIE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 4L + RID_ENDS_WITH_S) { // 4S
			if (Arrays.asList( //
					"King E." + _SLASH_ + "Raby", //
					_13_AVE_24_JUIN //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(_13_AVE_24_JUIN, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					DEPOT, //
					CARREFOUR_DE_L_ESTRIE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CARREFOUR_DE_L_ESTRIE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 6L) {
			if (Arrays.asList( //
					"Galt O." + _SLASH_ + LISIEUX, //
					U_DE_S //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(U_DE_S, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					LISIEUX_BRÛLÉ, //
					LISIEUX_LACHINE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(LISIEUX_LACHINE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 7L) {
			if (Arrays.asList( //
					CEGEP, // <>
					"King" + _SLASH_ + "Wellington", //
					DEPOT, //
					CHUS_URGENCE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHUS_URGENCE, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					CEGEP, // <>
					_13_AVE_24_JUIN, //
					"Belvédère" + _SLASH_ + "Hallée", //
					HABITAT_ANDRE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(HABITAT_ANDRE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 7L + RID_ENDS_WITH_S) { // 7S
			if (Arrays.asList( //
					DEPOT, //
					CARREFOUR_DE_L_ESTRIE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CARREFOUR_DE_L_ESTRIE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 8L) {
			if (Arrays.asList( //
					"Brûlotte" + _SLASH_ + "Montpellier", //
					"King" + _SLASH_ + "Wellington", //
					DEPOT, // <>
					CHUS_URGENCE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHUS_URGENCE, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					DEPOT, // <>
					_13_AVE_24_JUIN, //
					U_DE_S //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(U_DE_S, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 9L) {
			if (Arrays.asList( //
					CEGEP, //
					PLACE_FLEURIMONT, //
					"King E." + _SLASH_ + "Raby", //
					CHARDONNERETS_MARIKA //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHARDONNERETS_MARIKA, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					CEGEP, //
					"Allard" + _SLASH_ + "Normand", //
					U_DE_S //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(U_DE_S, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 11L) {
			if (Arrays.asList( //
					CARREFOUR_DE_L_ESTRIE, // SAME
					HABITAT_ANDRE, // SAME
					U_DE_S // SAME
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(U_DE_S, mTrip.getHeadsignId()); // SAME
				return true;
			}
			if (Arrays.asList( //
					CARREFOUR_DE_L_ESTRIE, // SAME
					HABITAT_ANDRE, // SAME
					U_DE_S, // SAME
					"Bl. Université (Triolet)", //
					"McGregor" + _SLASH_ + "Sauvignon", //
					PLATEAU_ST_JOSEPH //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(PLATEAU_ST_JOSEPH, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					CARREFOUR_DE_L_ESTRIE, // SAME
					HABITAT_ANDRE, // SAME
					U_DE_S, // SAME
					U_BISHOP_S //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(U_BISHOP_S, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 12L) {
			if (Arrays.asList( //
					DEPOT, //
					CEGEP //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CEGEP, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 14L) {
			if (Arrays.asList( //
					DEPOT, //
					CEGEP //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CEGEP, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 16L) {
			if (Arrays.asList( //
					FRONTENAC_BELVÉDÈRE, //
					PROSPECT + _SLASH_ + "Duvernay", //
					PROSPECT + _SLASH_ + "Ontario" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(PROSPECT + _SLASH_ + "Ontario", mTrip.getHeadsignId()); // FRONTENAC_BELVÉDÈRE
				return true;
			}
		} else if (mTrip.getRouteId() == 17L) {
			if (Arrays.asList( //
					CARREFOUR_DE_L_ESTRIE, //
					CEGEP //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CEGEP, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					"King O." + _SLASH_ + "Sauvé", //
					PLACE_DUSSAULT //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(PLACE_DUSSAULT, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 17L + RID_ENDS_WITH_S) { // 17S
			if (Arrays.asList( //
					CARREFOUR_DE_L_ESTRIE, //
					CEGEP //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CEGEP, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 19L) {
			if (Arrays.asList( //
					"Galt O." + _SLASH_ + LISIEUX, //
					CEGEP //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CEGEP, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					"Galt O." + _SLASH_ + LISIEUX, //
					LISIEUX_LACHINE, //
					LISIEUX_BRÛLÉ //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(LISIEUX_BRÛLÉ, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 21L) {
			if (Arrays.asList( //
					"Goddard" + _SLASH_ + "Duplessis", //
					CHUS_URGENCE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHUS_URGENCE, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					"King E." + _SLASH_ + "Lemire", //
					"Duplessis" + _SLASH_ + "Lemire" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Duplessis" + _SLASH_ + "Lemire", mTrip.getHeadsignId()); // King E." + _SLASH_ + "Lemire
				return true;
			}
		} else if (mTrip.getRouteId() == 22L) {
			if (Arrays.asList( //
					"King E." + _SLASH_ + "Raby", //
					PLACE_FLEURIMONT //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(PLACE_FLEURIMONT, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					PLACE_FLEURIMONT, //
					CHUS_URGENCE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHUS_URGENCE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 24L) {
			if (Arrays.asList( //
					ST_ROCH_É_FONTAINE, //
					"Lotbinière" + _SLASH_ + "North-Hatley" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Lotbinière" + _SLASH_ + "North-Hatley", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 25L) {
			if (Arrays.asList( //
					"Papineau" + _SLASH_ + "Coquelicots", //
					"Sanctuaire" + _SLASH_ + "Champêtre", //
					"Brûlotte (Gîte Du Bel-Âge)" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Brûlotte (Gîte Du Bel-Âge)", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 27L) {
			if (Arrays.asList( //
					ST_ROCH_É_FONTAINE, //
					U_DE_S //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(U_DE_S, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 28L) {
			if (Arrays.asList( //
					"Agriculture Canada", //
					"Beattie" + _SLASH_ + "Willowdale", //
					"St.Francis" + _SLASH_ + "Atto", //
					ALEXANDER_GALT //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(ALEXANDER_GALT, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 49L) {
			if (Arrays.asList( //
					"CHUS H.-D. (Bowen)", //
					NORTHROP_FRYE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(NORTHROP_FRYE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 50L) {
			if (Arrays.asList( //
					"Laliberté", //
					VAL_DES_ARBRES //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(VAL_DES_ARBRES, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 52L) {
			if (Arrays.asList( //
					ST_ROCH_É_FONTAINE, //
					"Plaza De L'Ouest" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Plaza De L'Ouest", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 53L) {
			if (Arrays.asList( //
					"CHUSFL (Porte 27A)", //
					CHUS_URGENCE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHUS_URGENCE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 55L) {
			if (Arrays.asList( //
					DEPOT, //
					CEGEP, // <>
					_13_AVE_24_JUIN //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(_13_AVE_24_JUIN, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					CEGEP, // <>
					MANOIR //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(MANOIR, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 57L) {
			if (Arrays.asList( //
					DEPOT, // <>
					"King" + _SLASH_ + "Wellington", // <>
					_13_AVE_24_JUIN //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(_13_AVE_24_JUIN, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					DEPOT, // <>
					"King" + _SLASH_ + "Wellington", // <>
					CARREFOUR_DE_L_ESTRIE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CARREFOUR_DE_L_ESTRIE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == RID_EXPR) {
			if (Arrays.asList( //
					_13_AVE_24_JUIN, // SAME
					CARREFOUR_DE_L_ESTRIE, // SAME
					CEGEP, // SAME
					DEPOT, // SAME
					LISIEUX_LACHINE // SAME
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(LISIEUX_LACHINE, mTrip.getHeadsignId()); // SAME
				return true;
			}
			if (Arrays.asList( //
					_13_AVE_24_JUIN, // SAME
					CARREFOUR_DE_L_ESTRIE, // SAME
					CEGEP, // SAME
					DEPOT, // SAME
					LISIEUX_LACHINE, // SAME
					"Pl. St-Joseph", //
					PROSPECT + _SLASH_ + "Duvernay", //
					"Frontenac" + _SLASH_ + "Belvédère", //
					"McGregor" + _SLASH_ + "Sauvignon", //
					CHUS_URGENCE, //
					NORTHROP_FRYE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(NORTHROP_FRYE, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					_13_AVE_24_JUIN, // SAME
					CARREFOUR_DE_L_ESTRIE, // SAME
					CEGEP, // SAME
					DEPOT, // SAME
					LISIEUX_LACHINE, // SAME
					PROSPECT + _SLASH_ + "Ontario", //
					IGA_EXTRA //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(IGA_EXTRA, mTrip.getHeadsignId());
				return true;
			}
		}
		System.out.printf("\nUnexpected trips to merge %s & %s!\n", mTrip, mTripToMerge);
		System.exit(-1);
		return false;
	}

	private static final Pattern STATION_DU = Pattern.compile("(station du )", Pattern.CASE_INSENSITIVE);
	private static final String STATION_DU_REPLACEMENT = StringUtils.EMPTY;

	private static final Pattern STATIONNEMENT = Pattern.compile("(^stat\\. |^stationnement )", Pattern.CASE_INSENSITIVE);
	private static final String STATIONNEMENT_REPLACEMENT = StringUtils.EMPTY;

	private static final Pattern UNIVERSITE_DE_SHERBROOKE = Pattern.compile("(université de sherbrooke)", Pattern.CASE_INSENSITIVE);
	private static final String UNIVERSITE_DE_SHERBROOKE_REPLACEMENT = U_DE_S;

	private static final Pattern UNIVERSITE_BISHOP = Pattern.compile("(université bishop's|université bishop|univerité bishop)", Pattern.CASE_INSENSITIVE);
	private static final String UNIVERSITE_BISHOP_REPLACEMENT = U_BISHOP_S;

	private static final Pattern DASH_ = Pattern.compile("( \\- )", Pattern.CASE_INSENSITIVE);
	private static final String DASH_REPLACEMENT = _SLASH_;

	public static final String CLEAN_ET_REPLACEMENT = "$2/$4";

	private static final Pattern PLATEAU = Pattern.compile("((^|\\W){1}(plateau)(\\W|$){1})", Pattern.CASE_INSENSITIVE);
	private static final String PLATEAU_REPLACEMENT = "$2" + PLATEAU_SHORT + "$4";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = DASH_.matcher(tripHeadsign).replaceAll(DASH_REPLACEMENT);
		tripHeadsign = STATION_DU.matcher(tripHeadsign).replaceAll(STATION_DU_REPLACEMENT);
		tripHeadsign = STATIONNEMENT.matcher(tripHeadsign).replaceAll(STATIONNEMENT_REPLACEMENT);
		tripHeadsign = UNIVERSITE_DE_SHERBROOKE.matcher(tripHeadsign).replaceAll(UNIVERSITE_DE_SHERBROOKE_REPLACEMENT);
		tripHeadsign = UNIVERSITE_BISHOP.matcher(tripHeadsign).replaceAll(UNIVERSITE_BISHOP_REPLACEMENT);
		tripHeadsign = AVENUE.matcher(tripHeadsign).replaceAll(AVENUE_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_ET.matcher(tripHeadsign).replaceAll(CLEAN_ET_REPLACEMENT);
		tripHeadsign = PLATEAU.matcher(tripHeadsign).replaceAll(PLATEAU_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
	}

	private static final Pattern AVENUE = Pattern.compile("( avenue)", Pattern.CASE_INSENSITIVE);
	private static final String AVENUE_REPLACEMENT = " " + AVE;

	private static final Pattern QUAI = Pattern.compile("( Quai )", Pattern.CASE_INSENSITIVE);
	private static final String QUAI_REPLACEMENT = " Q. ";

	private static final Pattern NO = Pattern.compile("(\\(no\\.([\\d]+)\\))", Pattern.CASE_INSENSITIVE);
	private static final String NO_REPLACEMENT = "#$2";

	private static final Pattern STATIONNEMENT_ALTERNATIF = Pattern.compile("((stat\\. alternatif|stationnement alternatif)[\\s]*(.*))",
			Pattern.CASE_INSENSITIVE);
	private static final String STATIONNEMENT_ALTERNATIF_REPLACEMENT = "$3 ($2)";

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = STATIONNEMENT_ALTERNATIF.matcher(gStopName).replaceAll(STATIONNEMENT_ALTERNATIF_REPLACEMENT);
		gStopName = STATION_DU.matcher(gStopName).replaceAll(STATION_DU_REPLACEMENT);
		gStopName = UNIVERSITE_DE_SHERBROOKE.matcher(gStopName).replaceAll(UNIVERSITE_DE_SHERBROOKE_REPLACEMENT);
		gStopName = UNIVERSITE_BISHOP.matcher(gStopName).replaceAll(UNIVERSITE_BISHOP_REPLACEMENT);
		gStopName = AVENUE.matcher(gStopName).replaceAll(AVENUE_REPLACEMENT);
		gStopName = QUAI.matcher(gStopName).replaceAll(QUAI_REPLACEMENT);
		gStopName = NO.matcher(gStopName).replaceAll(NO_REPLACEMENT);
		gStopName = CleanUtils.cleanStreetTypesFRCA(gStopName);
		return CleanUtils.cleanLabelFR(gStopName);
	}

	private static final String A = "A";
	private static final String B = "B";
	private static final String C = "C";
	private static final String D = "D";
	private static final String E = "E";
	private static final String F = "F";
	private static final String G = "G";
	private static final String H = "H";
	private static final String I = "I";
	private static final String J = "J";
	private static final String K = "K";

	@Override
	public int getStopId(GStop gStop) {
		if (StringUtils.isNumeric(gStop.getStopId())) {
			return Integer.parseInt(gStop.getStopId());
		}
		Matcher matcher = DIGITS.matcher(gStop.getStopId());
		if (matcher.find()) {
			int digits = Integer.parseInt(matcher.group());
			int stopId = 0;
			if (gStop.getStopId().endsWith(A)) {
				stopId += 10000;
			} else if (gStop.getStopId().endsWith(B)) {
				stopId += 20000;
			} else if (gStop.getStopId().endsWith(C)) {
				stopId += 30000;
			} else if (gStop.getStopId().endsWith(D)) {
				stopId += 40000;
			} else if (gStop.getStopId().endsWith(E)) {
				stopId += 50000;
			} else if (gStop.getStopId().endsWith(F)) {
				stopId += 60000;
			} else if (gStop.getStopId().endsWith(G)) {
				stopId += 70000;
			} else if (gStop.getStopId().endsWith(H)) {
				stopId += 80000;
			} else if (gStop.getStopId().endsWith(I)) {
				stopId += 90000;
			} else if (gStop.getStopId().endsWith(J)) {
				stopId += 100000;
			} else if (gStop.getStopId().endsWith(K)) {
				stopId += 110000;
			} else {
				System.out.printf("\nStop doesn't have an ID (end with) %s!\n", gStop);
				System.exit(-1);
			}
			return stopId + digits;
		}
		System.out.printf("\nUnexpected stop ID for %s!\n", gStop);
		System.exit(-1);
		return -1;
	}
}
