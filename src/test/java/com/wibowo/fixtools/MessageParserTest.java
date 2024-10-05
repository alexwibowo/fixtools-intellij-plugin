package com.wibowo.fixtools;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.wibowo.fixtools.intellij.model.MessageParser;
import com.wibowo.fixtools.intellij.model.MessageTree;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;

class MessageParserTest {

    private DataDictionary tradingDictionary;

    @BeforeEach
    void setUp() throws ConfigError {
        tradingDictionary = new DataDictionary(Thread.currentThread().getContextClassLoader().getResourceAsStream("FIX42-TRADING.xml"));
    }

    @Test
    void testDeepNested() throws FieldNotFound {
        final String s = "8=FIX.4.2^A9=680^A35=AB^A34=12496^A49=SD_RFQ_ESALES_AU_TR^A52=20240229-00:05:07.631^A56=RFS_TR^A"
                + "1=F001111^A11=S124^A15=USD^A40=D^A55=NZD/USD^A60=20240229-00:05:07.630^A109=F001111^A117=1708833869115461640^A167=HR_ETU^A1080=S124051111.111111^A1081=6^A34001=SD^A90038=somebody^A555=2^A"
                + "566=0.60945^A588=20240229^A624=2^A685=34000^A20192=0.60955^A20193=-0.0001^A80013=0.601^A"
                + "670=1^A671=F001111^A672=1^A673=34000^A20054=B^A"
                + "566=0.60955^A588=20240301^A624=1^A685=34000^A20192=0.60955^A20193=0^A80013=0.6011^A"
                + "670=1^A671=F001111^A672=1^A673=34000^A20054=B^A10=178^A";
        final Message parsed = parseMessage(s, tradingDictionary);
        final MessageTree messageTree = new MessageTree(parsed, tradingDictionary);

        final Map<String, Object> headerValues = messageTree.getHeaderValues();
        Assertions.assertThat(headerValues)
                .containsEntry("BeginString", "FIX.4.2")
                .containsEntry("BodyLength", "680")
                .containsEntry("MsgType", "AB")
                .containsEntry("MsgSeqNum", "12496")
                .containsEntry("SenderCompID", "SD_RFQ_ESALES_AU_TR")
                .containsEntry("SendingTime", "20240229-00:05:07.631")
                .containsEntry("TargetCompID", "RFS_TR");
        final Map<String, Pair<Object, String>> bodyValues = messageTree.getBodyValues();
        Assertions.assertThat(bodyValues)
                .containsEntry("Account", Pair.of("F001111", null))
                .containsEntry("ClOrdID", Pair.of("S124", null))
                .containsEntry("Currency", Pair.of("USD", null))
                .containsEntry("OrdType", Pair.of("D", "PREVIOUSLY_QUOTED"))
                .containsEntry("Symbol", Pair.of("NZD/USD", null))
                .containsEntry("TransactTime", Pair.of("20240229-00:05:07.630", null))
                .containsEntry("ClientID", Pair.of("F001111", null))
                .containsEntry("QuoteID", Pair.of("1708833869115461640", null))
                .containsEntry("SecurityType", Pair.of("HR_ETU", "HR_ETU"))
                .containsEntry("RefOrderID", Pair.of("S124051111.111111", null))
                .containsEntry("RefOrderIDSource", Pair.of("6", "QuoteReqID"))
                .containsEntry("VenueID", Pair.of("SD", null))
                .containsEntry("PriceInitiator", Pair.of("somebody", null));

        Assertions.assertThat(bodyValues).containsKey("NoLegs");

        final List<?> legList = (List<?>) bodyValues.get("NoLegs").getLeft();
        Assertions.assertThat(legList).hasSize(2);

        final Map<String, Pair<Object, String>> leg1Map = (Map<String, Pair<Object, String>>) legList.get(0);
        Assertions.assertThat(leg1Map)
                .containsEntry("LegPrice", Pair.of("0.60945", null))
                .containsEntry("LegSettlDate", Pair.of("20240229", null))
                .containsEntry("LegSide", Pair.of("2", "SELL"))
                .containsEntry("LegOrderQty", Pair.of("34000", null))
                .containsEntry("LegForwardPoints", Pair.of("-0.0001", null))
                .containsEntry("LegHistoricRate", Pair.of("0.601", null))
                .containsKey("NoLegAllocs");

        final List leg1Allocs = (List) leg1Map.get("NoLegAllocs").getLeft();
        Assertions.assertThat(leg1Allocs).hasSize(1);
        Assertions.assertThat((Map<String, Pair<Object, String>>) leg1Allocs.get(0))
                .containsEntry("LegIndividualAllocID", Pair.of("1", null))
                .containsEntry("LegAllocAccount", Pair.of("F001111", null))
                .containsEntry("LegAllocQty", Pair.of("34000", null))
                .containsEntry("LegAllocSide", Pair.of("B", "AS_DEFINED"));

        final Map<String, Pair<Object, String>> leg2Map = (Map<String, Pair<Object, String>>) legList.get(1);
        Assertions.assertThat(leg2Map)
                .containsEntry("LegPrice", Pair.of("0.60955", null))
                .containsEntry("LegSettlDate", Pair.of("20240301", null))
                .containsEntry("LegSide", Pair.of("1", "BUY"))
                .containsEntry("LegOrderQty", Pair.of("34000", null))
                .containsEntry("LegForwardPoints", Pair.of("0", null))
                .containsEntry("LegHistoricRate", Pair.of("0.6011", null))
                .containsKey("NoLegAllocs");
        final List leg2Allocs = (List) leg2Map.get("NoLegAllocs").getLeft();
        Assertions.assertThat(leg2Allocs).hasSize(1);
        Assertions.assertThat((Map<String, Pair<Object, String>>) leg2Allocs.get(0))
                .containsEntry("LegIndividualAllocID", Pair.of("1", null))
                .containsEntry("LegAllocAccount", Pair.of("F001111", null))
                .containsEntry("LegAllocQty", Pair.of("34000", null))
                .containsEntry("LegAllocSide", Pair.of("B", "AS_DEFINED"));
    }

    @Test
    void testNested() throws FieldNotFound {
        final String s = "8=FIX.4.2^A9=680^A35=AB^A34=12496^A49=RFQ_SALES_TR^A52=20240229-00:05:07.631^A56=RFS_TR^A1=FCS0026362^A11=S1112345^A15=USD^A40=D^A55=NZD/USD^A60=20240229-00:05:07.630^A109=F001111^A117=1708833869115461640^A167=HR_ETU^A1080=S12234.123245^A555=1^A"
                + "566=0.60945^A588=20240229^A624=2^A685=34000^A"
                + "566=0.60955^A588=20240301^A624=1^A685=34000^A"
                + "10=141^A";
        final Message parsed = parseMessage(s, tradingDictionary);
        final MessageTree messageTree = new MessageTree(parsed, tradingDictionary);
        System.out.println("finished " + messageTree);
    }

    private Message parseMessage(String source, DataDictionary dictionary) {
        return new MessageParser()
                .parse(dictionary, source.replaceAll("\\^A", "\001"));
    }


}
