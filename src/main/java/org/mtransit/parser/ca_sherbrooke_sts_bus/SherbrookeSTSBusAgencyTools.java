package org.mtransit.parser.ca_sherbrooke_sts_bus;

import static org.mtransit.commons.Constants.SPACE_;
import static org.mtransit.commons.RegexUtils.DIGITS;
import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://www.donneesquebec.ca/recherche/fr/dataset/transport-sts
public class SherbrookeSTSBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new SherbrookeSTSBusAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_FR;
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@Nullable
	@Override
	public String getAgencyId() {
		return "0"; // exclude "Service opéré par Taxis de Sherbrooke" // Taxibus
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "STS";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true;
	}

	// private static final String GRID_EXPR = "EXPR";
	private static final long RID_EXPR = 9_999L;

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
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

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR = "0A3D53";

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
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

	private static final Pattern UNIVERSITE_DE_SHERBROOKE = CleanUtils.cleanWord("université de sherbrooke");
	private static final String UNIVERSITE_DE_SHERBROOKE_REPLACEMENT = CleanUtils.cleanWordsReplacement("UdeS");

	private static final Pattern UNIVERSITE_BISHOP = CleanUtils.cleanWords("université bishop's", "université bishop", "univerité bishop");
	private static final String UNIVERSITE_BISHOP_REPLACEMENT = CleanUtils.cleanWordsReplacement("U Bishop'S");

	private static final Pattern DASH_ = Pattern.compile("( - )", Pattern.CASE_INSENSITIVE);

	private static final Pattern PLATEAU = CleanUtils.cleanWord("plateau");
	private static final String PLATEAU_REPLACEMENT = CleanUtils.cleanWordsReplacement("Pl");

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
	public @NotNull String cleanStopOriginalId(@NotNull String gStopId) {
		return CleanUtils.cleanMergedID(gStopId);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) {
		//noinspection deprecation
		final String stopId1 = cleanStopOriginalId(gStop.getStopId());
		if (StringUtils.isNumeric(stopId1)) {
			return Integer.parseInt(stopId1);
		}
		final Matcher matcher = DIGITS.matcher(stopId1);
		if (matcher.find()) {
			final int digits = Integer.parseInt(matcher.group());
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
				throw new MTLog.Fatal("Stop doesn't have an ID (end with) %s!", gStop.toStringPlus(true));
			}
			return stopId + digits;
		}
		throw new MTLog.Fatal("Unexpected stop ID for %s!", gStop.toStringPlus(true));
	}
}
