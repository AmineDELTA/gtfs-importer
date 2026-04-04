package com.amine.gtfs;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.amine.gtfs.services.GtfsParserUtils;

public class GtfsParserUtilsTest {
    // Latitude tests
    @Test
    public void parseLatitude_shouldReturnNull_forInvalidInputs() {
        assertAll(
                () -> assertNull(GtfsParserUtils.parseLatitude("invalid")),
                () -> assertNull(GtfsParserUtils.parseLatitude(null)),
                () -> assertNull(GtfsParserUtils.parseLatitude("")),
                () -> assertNull(GtfsParserUtils.parseLatitude("   ")),
                () -> assertNull(GtfsParserUtils.parseLatitude("100.0")),
                () -> assertNull(GtfsParserUtils.parseLatitude("90.1"))
        );
    }

    @Test
    public void parseLatitude_shouldRespectBoundaries_forValidInputs() {
        assertAll(
                () -> assertEquals(45.0, GtfsParserUtils.parseLatitude("45.0")),
                () -> assertEquals(90.0, GtfsParserUtils.parseLatitude("90.0")),
                () -> assertEquals(-90.0, GtfsParserUtils.parseLatitude("-90.0"))
        );
    }

    // Longitude tests
    @Test
    public void parseLongitude_shouldReturnNull_forInvalidInputs() {
        assertAll(
                () -> assertNull(GtfsParserUtils.parseLongitude("invalid")),
                () -> assertNull(GtfsParserUtils.parseLongitude(null)),
                () -> assertNull(GtfsParserUtils.parseLongitude("")),
                () -> assertNull(GtfsParserUtils.parseLongitude("   ")),
                () -> assertNull(GtfsParserUtils.parseLongitude("200.0")),
                () -> assertNull(GtfsParserUtils.parseLongitude("180.1"))
        );
    }

    @Test
    public void parseLongitude_shouldRespectBoundaries_forValidInputs() {
        assertAll(
                () -> assertEquals(45.0, GtfsParserUtils.parseLongitude("45.0")),
                () -> assertEquals(180.0, GtfsParserUtils.parseLongitude("180.0")),
                () -> assertEquals(-180.0, GtfsParserUtils.parseLongitude("-180.0"))
        );
    }

    // ParseDouble tests
    @Test
    public void parseDouble_shouldReturnNull_forInvalidInputs() {
        assertAll(
                () -> assertNull(GtfsParserUtils.parseDouble("invalid")),
                () -> assertNull(GtfsParserUtils.parseDouble(null)),
                () -> assertNull(GtfsParserUtils.parseDouble("")),
                () -> assertNull(GtfsParserUtils.parseDouble("   ")),
                () -> assertNull(GtfsParserUtils.parseDouble("Infinity")),
                () -> assertNull(GtfsParserUtils.parseDouble("-Infinity")),
                () -> assertNull(GtfsParserUtils.parseDouble("NaN"))
        );
    }

    @Test
    public void parseDouble_shouldParseValue_forValidInput() {
        assertEquals(45.0, GtfsParserUtils.parseDouble("45.0"));
    }

    // ParseInteger tests
    @Test
    public void parseInteger_shouldReturnNull_forInvalidInputs() {
        assertAll(
                () -> assertNull(GtfsParserUtils.parseInteger("invalid")),
                () -> assertNull(GtfsParserUtils.parseInteger(null)),
                () -> assertNull(GtfsParserUtils.parseInteger("")),
                () -> assertNull(GtfsParserUtils.parseInteger("   ")),
                () -> assertNull(GtfsParserUtils.parseInteger("45.0"))
        );
    }

    @Test
    public void parseInteger_shouldParseValue_forValidInputs() {
        assertAll(
                () -> assertEquals(45, GtfsParserUtils.parseInteger("45")),
                () -> assertEquals(45, GtfsParserUtils.parseInteger("   45   "))
        );
    }

    // ParseHeader tests
    @Test
    public void parseHeader_shouldMapColumnsToIndices_forValidInput() {
        var headerMap = GtfsParserUtils.parseHeader("field1,field2,field3");

        assertAll(
                () -> assertEquals(3, headerMap.size()),
                () -> assertEquals(0, headerMap.get("field1")),
                () -> assertEquals(1, headerMap.get("field2")),
                () -> assertEquals(2, headerMap.get("field3"))
        );
    }

    @Test
    public void parseHeader_shouldReturnEmptyMap_forNullOrBlankInput() {
        assertAll(
                () -> assertEquals(0, GtfsParserUtils.parseHeader("").size()),
                () -> assertEquals(0, GtfsParserUtils.parseHeader("   ").size()),
                () -> assertEquals(0, GtfsParserUtils.parseHeader(null).size())
        );
    }

    @Test
    public void parseHeader_shouldTrimColumns_forWhitespacePaddedInput() {
        var headerMap = GtfsParserUtils.parseHeader(" field1 , field2 , field3 ");

        assertAll(
                () -> assertEquals(3, headerMap.size()),
                () -> assertEquals(0, headerMap.get("field1")),
                () -> assertEquals(1, headerMap.get("field2")),
                () -> assertEquals(2, headerMap.get("field3"))
        );
    }

    @Test
    public void parseHeader_shouldKeepEmptyColumn_forInputWithConsecutiveCommas() {
        var headerMap = GtfsParserUtils.parseHeader("field1,,field3");

        assertAll(
                () -> assertEquals(3, headerMap.size()),
                () -> assertEquals(0, headerMap.get("field1")),
                () -> assertEquals(1, headerMap.get("")),
                () -> assertEquals(2, headerMap.get("field3"))
        );
    }

    // HasRequiredColumns tests
    @Test
    public void hasRequiredColumns_shouldReturnTrue_whenAllRequiredColumnsExist() {
        assertTrue(GtfsParserUtils.hasRequiredColumns(
                GtfsParserUtils.parseHeader("field1,field2,field3"),
                "field1",
                "field2"
        ));
    }

    @Test
    public void hasRequiredColumns_shouldReturnFalse_whenRequiredColumnIsMissing() {
        assertFalse(GtfsParserUtils.hasRequiredColumns(
                GtfsParserUtils.parseHeader("field1,field2,field3"),
                "field1",
                "field4"
        ));
    }

    @Test
    public void hasRequiredColumns_shouldReturnFalse_forEmptyOrNullHeader() {
        assertAll(
                () -> assertFalse(GtfsParserUtils.hasRequiredColumns(GtfsParserUtils.parseHeader(""), "field1")),
                () -> assertFalse(GtfsParserUtils.hasRequiredColumns(GtfsParserUtils.parseHeader(null), "field1"))
        );
    }

    @Test
    public void hasRequiredColumns_shouldReturnFalse_whenRequiredColumnsAreNull() {
        assertFalse(GtfsParserUtils.hasRequiredColumns(
                GtfsParserUtils.parseHeader("field1,field2,field3"),
                (String[]) null
        ));
    }

    // IsValidGtfsRouteType tests
    @Test
    public void isValidGtfsRouteType_shouldReturnTrue_forSupportedGtfsTypes() {
        assertAll(
                () -> assertTrue(GtfsParserUtils.isValidGtfsRouteType(0)),
                () -> assertTrue(GtfsParserUtils.isValidGtfsRouteType(1)),
                () -> assertTrue(GtfsParserUtils.isValidGtfsRouteType(2)),
                () -> assertTrue(GtfsParserUtils.isValidGtfsRouteType(3)),
                () -> assertTrue(GtfsParserUtils.isValidGtfsRouteType(4)),
                () -> assertTrue(GtfsParserUtils.isValidGtfsRouteType(5)),
                () -> assertTrue(GtfsParserUtils.isValidGtfsRouteType(6)),
                () -> assertTrue(GtfsParserUtils.isValidGtfsRouteType(7)),
                () -> assertTrue(GtfsParserUtils.isValidGtfsRouteType(11)),
                () -> assertTrue(GtfsParserUtils.isValidGtfsRouteType(12))
        );
    }

    @Test
    public void isValidGtfsRouteType_shouldReturnFalse_forUnsupportedGtfsTypes() {
        assertAll(
                () -> assertFalse(GtfsParserUtils.isValidGtfsRouteType(8)),
                () -> assertFalse(GtfsParserUtils.isValidGtfsRouteType(9)),
                () -> assertFalse(GtfsParserUtils.isValidGtfsRouteType(10)),
                () -> assertFalse(GtfsParserUtils.isValidGtfsRouteType(13)),
                () -> assertFalse(GtfsParserUtils.isValidGtfsRouteType(-1))
        );
    }

    // ParseType tests
    @Test
    public void parseType_shouldReturnType_forValidRouteTypes() {
        assertAll(
                () -> assertEquals(0, GtfsParserUtils.parseType("0")),
                () -> assertEquals(7, GtfsParserUtils.parseType("7")),
                () -> assertEquals(11, GtfsParserUtils.parseType("11")),
                () -> assertEquals(12, GtfsParserUtils.parseType("12"))
        );
    }

    @Test
    public void parseType_shouldReturnNull_forUnsupportedRouteTypes() {
        assertAll(
                () -> assertNull(GtfsParserUtils.parseType("8")),
                () -> assertNull(GtfsParserUtils.parseType("9")),
                () -> assertNull(GtfsParserUtils.parseType("10")),
                () -> assertNull(GtfsParserUtils.parseType("13")),
                () -> assertNull(GtfsParserUtils.parseType("-1"))
        );
    }

    @Test
    public void parseType_shouldReturnNull_forInvalidNumericInputs() {
        assertAll(
                () -> assertNull(GtfsParserUtils.parseType("invalid")),
                () -> assertNull(GtfsParserUtils.parseType("")),
                () -> assertNull(GtfsParserUtils.parseType("   ")),
                () -> assertNull(GtfsParserUtils.parseType(null))
        );
    }
}