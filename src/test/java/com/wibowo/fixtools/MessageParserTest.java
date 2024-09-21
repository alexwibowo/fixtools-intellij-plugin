package com.wibowo.fixtools;

import java.util.List;
import java.util.Map;

import com.wibowo.fixtools.intellij.model.MessageParser;
import com.wibowo.fixtools.intellij.model.MessageTree;
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
        final String s = "8=FIX.4.2^A9=680^A35=AB^A34=12496^A49=SD_RFQ_ESALES_AU_TR^A52=20240229-00:05:07.631^A56=ANZ_RFS_TR^A"
                + "1=FCS0026362^A11=S12405900608^A15=USD^A40=D^A55=NZD/USD^A60=20240229-00:05:07.630^A109=FCS0026362^A117=1708833869115461640^A167=HR_ETU^A1080=S12405900608.1709165093046^A1081=6^A34001=SD^A90038=simpsom1^A555=2^A"
                + "566=0.60945^A588=20240229^A624=2^A685=34000^A20192=0.60955^A20193=-0.0001^A80013=0.601^A"
                + "670=1^A671=FCS0026362^A672=1^A673=34000^A20054=B^A"
                + "566=0.60955^A588=20240301^A624=1^A685=34000^A20192=0.60955^A20193=0^A80013=0.6011^A"
                + "670=1^A671=FCS0026362^A672=1^A673=34000^A20054=B^A10=177^A";
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
                .containsEntry("TargetCompID", "ANZ_RFS_TR");
        final Map<String, Object> bodyValues = messageTree.getBodyValues();
        Assertions.assertThat(bodyValues)
                .containsEntry("Account", "FCS0026362")
                .containsEntry("ClOrdID", "S12405900608")
                .containsEntry("Currency", "USD")
                .containsEntry("OrdType", "D")
                .containsEntry("Symbol", "NZD/USD")
                .containsEntry("TransactTime", "20240229-00:05:07.630")
                .containsEntry("ClientID", "FCS0026362")
                .containsEntry("QuoteID", "1708833869115461640")
                .containsEntry("SecurityType", "HR_ETU")
                .containsEntry("RefOrderID", "S12405900608.1709165093046")
                .containsEntry("RefOrderIDSource", "6")
                .containsEntry("VenueID", "SD")
                .containsEntry("PriceInitiator", "simpsom1");

        Assertions.assertThat(bodyValues).containsKey("NoLegs");

        final List<?> legList = (List<?>) bodyValues.get("NoLegs");
        Assertions.assertThat(legList).hasSize(2);

        final Map<String, Object> leg1Map = (Map<String, Object>) legList.get(0);
        Assertions.assertThat(leg1Map)
                .containsEntry("LegPrice", "0.60945")
                .containsEntry("LegSettlDate", "20240229")
                .containsEntry("LegSide", "2")
                .containsEntry("LegOrderQty", "34000")
                .containsEntry("LegForwardPoints", "-0.0001")
                .containsEntry("LegHistoricRate", "0.601")
                .containsKey("NoLegAllocs");

        final List leg1Allocs = (List) leg1Map.get("NoLegAllocs");
        Assertions.assertThat(leg1Allocs).hasSize(1);
        Assertions.assertThat((Map<String, Object>) leg1Allocs.get(0))
                .containsEntry("LegIndividualAllocID", "1")
                .containsEntry("LegAllocAccount", "FCS0026362")
                .containsEntry("LegAllocQty", "34000")
                .containsEntry("LegAllocSide", "B");

        final Map<String, Object> leg2Map = (Map<String, Object>) legList.get(1);
        Assertions.assertThat(leg2Map)
                .containsEntry("LegPrice", "0.60955")
                .containsEntry("LegSettlDate", "20240301")
                .containsEntry("LegSide", "1")
                .containsEntry("LegOrderQty", "34000")
                .containsEntry("LegForwardPoints", "0")
                .containsEntry("LegHistoricRate", "0.6011")
                .containsKey("NoLegAllocs");
        final List leg2Allocs = (List) leg2Map.get("NoLegAllocs");
        Assertions.assertThat(leg2Allocs).hasSize(1);
        Assertions.assertThat((Map<String, Object>) leg2Allocs.get(0))
                .containsEntry("LegIndividualAllocID", "1")
                .containsEntry("LegAllocAccount", "FCS0026362")
                .containsEntry("LegAllocQty", "34000")
                .containsEntry("LegAllocSide", "B");
    }

    @Test
    void testNested() throws FieldNotFound {
        final String s = "8=FIX.4.2^A9=680^A35=AB^A34=12496^A49=SD_RFQ_ESALES_AU_TR^A52=20240229-00:05:07.631^A56=ANZ_RFS_TR^A1=FCS0026362^A11=S12405900608^A15=USD^A40=D^A55=NZD/USD^A60=20240229-00:05:07.630^A109=FCS0026362^A117=1708833869115461640^A167=HR_ETU^A1080=S12405900608.1709165093046^A555=1^A"
                + "566=0.60945^A588=20240229^A624=2^A685=34000^A"
                + "566=0.60955^A588=20240301^A624=1^A685=34000^A"
                + "10=85^A";
        final Message parsed = parseMessage(s, tradingDictionary);
        final MessageTree messageTree = new MessageTree(parsed, tradingDictionary);
        System.out.println("finished " + messageTree);
    }

    private Message parseMessage(String source, DataDictionary dictionary) {
        return new MessageParser()
                .parse(dictionary, source.replaceAll("\\^A", "\001"));
    }


}
