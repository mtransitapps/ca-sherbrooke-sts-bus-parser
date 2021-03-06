package org.mtransit.parser.ca_sherbrooke_sts_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MTrip;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mtransit.commons.Constants.SPACE_;
import static org.mtransit.parser.StringUtils.EMPTY;

// https://www.donneesquebec.ca/recherche/fr/dataset/transport-sts
// CURRENT: https://www.donneesquebec.ca/recherche/dataset/e82b9141-09d8-4f85-af37-d84937bc2503/resource/b7f43b2a-2557-4e3b-ba12-5a5c6d4de5b1/download/gtfsstsherbrooke.zip
// NEXT:    https://www.donneesquebec.ca/recherche/dataset/e82b9141-09d8-4f85-af37-d84937bc2503/resource/f1e525ec-2d03-4421-84d9-0b2499a554ad/download/gtfs_donneesouvertes_stsh_ete2021_20210601.zip
public class SherbrookeSTSBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new SherbrookeSTSBusAgencyTools().start(args);
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@Override
	public String getAgencyName() {
		return "STS";
	}

	@NotNull
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
	public long getRouteId(@NotNull GRoute gRoute) {
		if (StringUtils.isNumeric(gRoute.getRouteShortName())) {
			return Long.parseLong(gRoute.getRouteShortName());
		}
		if (GRID_EXPR.equals(gRoute.getRouteShortName())) {
			return RID_EXPR;
		}
		final Matcher matcher = DIGITS.matcher(gRoute.getRouteShortName());
		if (matcher.find()) {
			final long digits = Long.parseLong(matcher.group());
			if (gRoute.getRouteShortName().endsWith(S)) {
				return digits + RID_ENDS_WITH_S;
			} else if (gRoute.getRouteShortName().endsWith(X)) {
				return digits + RID_ENDS_WITH_X;
			}
		}
		throw new MTLog.Fatal("Unexpected route ID for %s!", gRoute);
	}

	private static final String RTS_EXPRESS = "E";

	@Nullable
	@Override
	public String getRouteShortName(@NotNull GRoute gRoute) {
		if (GRID_EXPR.equals(gRoute.getRouteShortName())) {
			return RTS_EXPRESS;
		}
		return super.getRouteShortName(gRoute);
	}

	private static final String RLN_SPLIT = "-";

	// http://www.toponymie.gouv.qc.ca/ct/normes-procedures/terminologie-geographique/liste-termes-geographiques.html
	private static final String AVE = "Av";
	private static final String CARREFOUR = "Carref";
	private static final String PARC = "Parc";
	private static final String PLACE = "Pl";
	private static final String PLATEAU_SHORT = "Pl";
	private static final String PARC_INDUSTRIEL = PARC + " Ind";
	private static final String TERRASSES = "Tsses";

	private static final String _SLASH_ = " / ";

	private static final String U_DE_S = "UdeS";
	private static final String U_BISHOP_S = "U Bishop'S";
	private static final String MANOIR = "Manoir";
	private static final String DU = "Du";
	private static final String CHAMPETRE = "Champêtre";
	private static final String GITE_DU_BEL_AGE = "Gite Du Bel Âge";
	private static final String ALEXANDER_GALT = "Alexander-Galt";
	private static final String VAL_DES_ARBRES = "Val-Des-Arbres";
	private static final String ATTO = "Atto";
	private static final String COQUELICOTS = "Coquelicots";
	private static final String LALIBERTE = "Laliberté";
	private static final String ROCK_FOREST = "Rock-Forest";
	private static final String NORTH_HATLEY = "North Hatley";
	private static final String LOTBINIERE = "Lotbinière";
	private static final String BOULOGNE = "Boulogne";
	private static final String ST_FRANCOIS = "St-François";
	private static final String FRONTIERE = "Frontière";
	private static final String BOURASSA = "Bourassa";
	private static final String DUSSAULT = "Dussault";
	private static final String ONTARIO = "Ontario";
	private static final String PROSPECT = "Prospect";
	private static final String BLANCHARD = "Blanchard";
	private static final String RABY = "Raby";
	private static final String LACHINE = "Lachine";
	private static final String HOTEL_DIEU = "Hôtel-Dieu";
	private static final String BOWEN = "Bowen";
	private static final String TALBOT = "Talbot";
	private static final String KING_OUEST = "King Ouest";
	private static final String IGA_EXTRA = "IGA Extra";
	private static final String NORMAND = "Normand";
	private static final String ST_JOSEPH = "St-Joseph";
	private static final String BRULE = "Brûlé";
	private static final String ANDRE = "André";
	private static final String HALLEE = "Hallée";
	private static final String CARREFOUR_DE_L_ESTRIE = CARREFOUR + " De L'Estrie";
	private static final String NORTHROP_FRYE = "Northrop-Frye";
	private static final String CHALUMEAU = "Chalumeau";
	private static final String OXFORD = "Oxford";
	private static final String FLEURIMONT = "Fleurimont";
	private static final String CHUS = "CHUS";
	private static final String CHUS_FLEURIMONT = CHUS + " " + FLEURIMONT;
	private static final String CHUS_HOTEL_DIEU = CHUS + " " + HOTEL_DIEU;
	private static final String PLACE_FLEURIMONT = PLACE + " " + FLEURIMONT;
	private static final String LISIEUX = "Lisieux";
	private static final String KRUGER = "Kruger";
	private static final String CHARDONNERETS = "Chardonnerets";
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
	private static final String PLACE_FLEURIMONT_GALVIN = PLACE_FLEURIMONT + _SLASH_ + GALVIN;
	private static final String LISIEUX_BRULE = LISIEUX + _SLASH_ + BRULE;
	private static final String LISIEUX_LACHINE = LISIEUX + _SLASH_ + LACHINE;
	private static final String ANDRE_HALLEE = ANDRE + _SLASH_ + HALLEE;
	private static final String RABY_NORMAND = RABY + _SLASH_ + NORMAND;
	private static final String ONTARIO_PROSPECT = ONTARIO + _SLASH_ + PROSPECT;
	private static final String IGA_EXTRA_KING_OUEST = IGA_EXTRA + _SLASH_ + KING_OUEST;
	private static final String BOWEN_TALBOT = BOWEN + _SLASH_ + TALBOT;
	private static final String BOURASSA_FRONTIERE = BOURASSA + _SLASH_ + FRONTIERE;
	private static final String ST_FRANCOIS_BOULOGNE = ST_FRANCOIS + _SLASH_ + BOULOGNE;
	private static final String _13_AVE_24_JUIN = "13e " + AVE + _SLASH_ + "24-Juin";
	private static final String LOTBINIERE_NORTH_HATLEY = LOTBINIERE + _SLASH_ + NORTH_HATLEY;
	private static final String U_BISHOP_S_OXFORD = U_BISHOP_S + _SLASH_ + OXFORD;
	private static final String GITE_DU_BEL_AGE_CHAMPETRE_COQUELICOTS = GITE_DU_BEL_AGE + _SLASH_ + CHAMPETRE + _SLASH_ + COQUELICOTS;
	private static final String ALEXANDER_GALT_BEATTLE_ATTO = ALEXANDER_GALT + _SLASH_ + BEATTLE + _SLASH_ + ATTO;
	private static final String VAL_DES_ARBRES_LALIBERTE = VAL_DES_ARBRES + _SLASH_ + LALIBERTE;

	private static final String IGA_EXTRA_KING_OUEST_NORTHROP_FRYE = IGA_EXTRA_KING_OUEST + " " + RLN_SPLIT + " " + NORTHROP_FRYE;
	private static final String RLN_1 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + BOWEN_TALBOT;
	private static final String RLN_2 = CEGEP + " " + RLN_SPLIT + " " + U_BISHOP_S_OXFORD;
	private static final String RLN_3 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + _13_AVE_24_JUIN;
	private static final String RLN_4 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + CHALUMEAU;
	private static final String RLN_5 = CEGEP + " " + RLN_SPLIT + " " + _13_AVE_24_JUIN;
	private static final String RLN_6 = U_DE_S + " " + RLN_SPLIT + " " + LISIEUX_LACHINE;
	private static final String RLN_7 = ANDRE_HALLEE + " " + RLN_SPLIT + " " + CHUS_FLEURIMONT;
	private static final String RLN_8 = U_DE_S + " " + RLN_SPLIT + " " + CHUS_FLEURIMONT;
	private static final String RLN_9 = U_DE_S + " " + RLN_SPLIT + " " + CHARDONNERETS;
	private static final String RLN_11 = U_BISHOP_S + " " + RLN_SPLIT + " " + PLATEAU_ST_JOSEPH;
	private static final String RLN_12 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + CEGEP;
	private static final String RLN_13 = U_DE_S + " " + RLN_SPLIT + " " + RABY_NORMAND;
	private static final String RLN_14 = U_DE_S + " " + RLN_SPLIT + " " + CEGEP;
	private static final String RLN_15 = U_DE_S + " " + RLN_SPLIT + " " + PARC_BLANCHARD;
	private static final String RLN_16 = U_DE_S + " " + RLN_SPLIT + " " + ONTARIO_PROSPECT;
	private static final String RLN_17 = CEGEP + " " + RLN_SPLIT + " " + PLACE_DUSSAULT;
	private static final String RLN_18 = U_DE_S + " " + RLN_SPLIT + " " + BOURASSA_FRONTIERE;
	private static final String RLN_19 = CEGEP + " " + RLN_SPLIT + LISIEUX_BRULE;
	private static final String RLN_20 = CEGEP + " " + RLN_SPLIT + " " + ST_FRANCOIS_BOULOGNE + " " + TAXI_BUS;
	private static final String RLN_21 = PLACE_FLEURIMONT + " " + RLN_SPLIT + " " + CHUS_FLEURIMONT + " " + TAXI_BUS;
	private static final String RLN_22 = PLACE_FLEURIMONT_GALVIN + " " + RLN_SPLIT + " " + CHUS_FLEURIMONT;
	private static final String RLN_24 = U_DE_S + " " + RLN_SPLIT + " " + LOTBINIERE_NORTH_HATLEY;
	private static final String RLN_25 = _13_AVE_24_JUIN + " " + RLN_SPLIT + " " + GITE_DU_BEL_AGE_CHAMPETRE_COQUELICOTS + " " + TAXI_BUS;
	private static final String RLN_26 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + PARC_INDUSTRIEL + " " + TAXI_BUS;
	private static final String RLN_27 = U_DE_S + " " + RLN_SPLIT + " " + VAL_DU_LAC;
	private static final String RLN_28 = U_BISHOP_S + " " + RLN_SPLIT + " " + ALEXANDER_GALT_BEATTLE_ATTO;
	private static final String RLN_29 = "Dépôt " + RLN_SPLIT + " " + U_DE_S;
	private static final String RLN_49 = NORTHROP_FRYE + " " + RLN_SPLIT + " " + CHUS_HOTEL_DIEU;
	private static final String RLN_50 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + VAL_DES_ARBRES_LALIBERTE;
	private static final String RLN_51 = CEGEP + " " + RLN_SPLIT + " " + KRUGER;
	private static final String RLN_52 = TERRASSES_ROCK_FOREST + " " + RLN_SPLIT + " " + AVE_DU_PARC;
	private static final String RLN_53 = U_DE_S + " " + RLN_SPLIT + " " + CAMPUS + " De La Santé";
	private static final String RLN_54 = NORTHROP_FRYE + " " + RLN_SPLIT + " " + CHUS_FLEURIMONT;
	private static final String RLN_55 = DU_MANOIR + " " + RLN_SPLIT + " " + _13_AVE_24_JUIN;
	private static final String RLN_56 = U_BISHOP_S + " " + RLN_SPLIT + " " + OXFORD + _SLASH_ + PROVIGO_LENNOXVILLE + " " + TAXI_BUS;
	private static final String RLN_57 = CARREFOUR_DE_L_ESTRIE + " " + RLN_SPLIT + " " + _13_AVE_24_JUIN;

	@NotNull
	@Override
	public String getRouteLongName(@NotNull GRoute gRoute) {
		String routeLongName = gRoute.getRouteLongNameOrDefault();
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
			throw new MTLog.Fatal("Unexpected route long name for %s!", gRoute);
		}
		return cleanRouteLongName(routeLongName);
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.SAINT.matcher(routeLongName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		routeLongName = UNIVERSITE_DE_SHERBROOKE.matcher(routeLongName).replaceAll(UNIVERSITE_DE_SHERBROOKE_REPLACEMENT);
		routeLongName = UNIVERSITE_BISHOP.matcher(routeLongName).replaceAll(UNIVERSITE_BISHOP_REPLACEMENT);
		routeLongName = CleanUtils.cleanStreetTypesFRCA(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR = "0A3D53";

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Nullable
	@Override
	public String getRouteColor(@NotNull GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteColor())) {
			if (GRID_EXPR.equals(gRoute.getRouteShortName())) {
				return "231F20";
			}
			try {
				final Matcher matcher = DIGITS.matcher(gRoute.getRouteShortName());
				if (matcher.find()) {
					final int digits = Integer.parseInt(matcher.group());
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
				throw new MTLog.Fatal("Unexpected route color %s!", gRoute);
			} catch (Exception e) {
				throw new MTLog.Fatal(e, "Unexpected route color %s!", gRoute);
			}
		}
		return super.getRouteColor(gRoute);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	@Override
	public boolean directionFinderEnabled(long routeId, @NotNull GRoute gRoute) {
		if (routeId == RID_EXPR) {
			return false; // Express route is a mess
		}
		return super.directionFinderEnabled(routeId, gRoute);
	}

	@Override
	public boolean mergeHeadsign(@NotNull MTrip mTrip, @NotNull MTrip mTripToMerge) {
		final List<String> headsignsValues = Arrays.asList(mTrip.getHeadsignValue(), mTripToMerge.getHeadsignValue());
		if (mTrip.getRouteId() == RID_EXPR) {
			if (Arrays.asList( //
					"Prospect / Ontario",
					"Carref De L'Estrie",
					"Stat IGA Extra" // STS web-site direction
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Stat IGA Extra", mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					"Pl St-Joseph",
					"Frontenac / Belvédère",
					"13e Av / 24-Juin",
					"McGregor / Sauvignon",
					"Stat Northrop-Frye" // STS web-site direction
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Stat Northrop-Frye", mTrip.getHeadsignId());
				return true;
			}
		}
		throw new MTLog.Fatal("Unexpected trips to merge %s & %s!", mTrip, mTripToMerge);
	}

	private static final Pattern UNIVERSITE_DE_SHERBROOKE = Pattern.compile("(université de sherbrooke)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
	private static final String UNIVERSITE_DE_SHERBROOKE_REPLACEMENT = U_DE_S;

	private static final Pattern UNIVERSITE_BISHOP = Pattern.compile("(université bishop's|université bishop|univerité bishop)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
	private static final String UNIVERSITE_BISHOP_REPLACEMENT = U_BISHOP_S;

	private static final Pattern DASH_ = Pattern.compile("( - )", Pattern.CASE_INSENSITIVE);

	private static final Pattern PLATEAU = Pattern.compile("((^|\\W)(plateau)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String PLATEAU_REPLACEMENT = "$2" + PLATEAU_SHORT + "$4";

	private static final Pattern ENDS_WITH_URGENCE_ = Pattern.compile("( \\(urgence\\)$)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = UNIVERSITE_DE_SHERBROOKE.matcher(tripHeadsign).replaceAll(UNIVERSITE_DE_SHERBROOKE_REPLACEMENT);
		tripHeadsign = UNIVERSITE_BISHOP.matcher(tripHeadsign).replaceAll(UNIVERSITE_BISHOP_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_ET.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_ET_REPLACEMENT);
		tripHeadsign = PLATEAU.matcher(tripHeadsign).replaceAll(PLATEAU_REPLACEMENT);
		tripHeadsign = ENDS_WITH_URGENCE_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.cleanBounds(Locale.FRENCH, tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
	}

	private static final Pattern NO = Pattern.compile("(\\(no\\.([\\d]+)\\))", Pattern.CASE_INSENSITIVE);
	private static final String NO_REPLACEMENT = "#$2";

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = DASH_.matcher(gStopName).replaceAll(SPACE_);
		gStopName = UNIVERSITE_DE_SHERBROOKE.matcher(gStopName).replaceAll(UNIVERSITE_DE_SHERBROOKE_REPLACEMENT);
		gStopName = UNIVERSITE_BISHOP.matcher(gStopName).replaceAll(UNIVERSITE_BISHOP_REPLACEMENT);
		gStopName = NO.matcher(gStopName).replaceAll(NO_REPLACEMENT);
		gStopName = CleanUtils.cleanBounds(Locale.FRENCH, gStopName);
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
	public int getStopId(@NotNull GStop gStop) {
		//noinspection deprecation
		final String stopId1 = gStop.getStopId();
		if (StringUtils.isNumeric(stopId1)) {
			return Integer.parseInt(stopId1);
		}
		Matcher matcher = DIGITS.matcher(stopId1);
		if (matcher.find()) {
			int digits = Integer.parseInt(matcher.group());
			int stopId = 0;
			if (stopId1.endsWith(A)) {
				stopId += 10000;
			} else if (stopId1.endsWith(B)) {
				stopId += 20000;
			} else if (stopId1.endsWith(C)) {
				stopId += 30000;
			} else if (stopId1.endsWith(D)) {
				stopId += 40000;
			} else if (stopId1.endsWith(E)) {
				stopId += 50000;
			} else if (stopId1.endsWith(F)) {
				stopId += 60000;
			} else if (stopId1.endsWith(G)) {
				stopId += 70000;
			} else if (stopId1.endsWith(H)) {
				stopId += 80000;
			} else if (stopId1.endsWith(I)) {
				stopId += 90000;
			} else if (stopId1.endsWith(J)) {
				stopId += 100000;
			} else if (stopId1.endsWith(K)) {
				stopId += 110000;
			} else {
				throw new MTLog.Fatal("Stop doesn't have an ID (end with) %s!", gStop);
			}
			return stopId + digits;
		}
		throw new MTLog.Fatal("Unexpected stop ID for %s!", gStop);
	}
}
