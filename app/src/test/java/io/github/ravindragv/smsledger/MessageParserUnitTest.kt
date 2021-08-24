package io.github.ravindragv.smsledger

import org.junit.Assert.assertEquals
import org.junit.Test

class MessageParserUnitTest {
    @Test
    fun testTransactionType() {
        val msgParser = MessageParser()
        val sampleMessages = TestConstants.getTestMessageSamples()

        println("Running testTransactionType tests")
        for (msg in sampleMessages) {
            //println("Test Message ${msg.smsMsg}")
            assertEquals(msg.transactionType, msgParser.getTransactionType(msg.smsMsg))
        }
    }

    @Test
    fun testTransactionAmount() {
        val msgParser = MessageParser()
        val sampleMessages = TestConstants.getTestMessageSamples()

        println("Running testTransactionAmount tests")
        for (msg in sampleMessages) {
            //println("Test Message ${msg.msgBody}")
            assertEquals(msg.transactionAmount, msgParser.getTransactionAmt(msg.smsMsg))
        }
    }

    @Test
    fun testAccountType() {
        val msgParser = MessageParser()
        val sampleMessages = TestConstants.getTestMessageSamples()

        println("Running testAccountType tests")
        for (msg in sampleMessages) {
            //println("Test Message ${msg.msgBody}")
            assertEquals(msg.accountType, msgParser.getAccountType(msg.smsMsg))
        }
    }

    @Test
    fun testAccountNumber() {
        val msgParser = MessageParser()
        val sampleMessages = TestConstants.getTestMessageSamples()

        println("Running testAccountNumber tests")
        for (msg in sampleMessages) {
            //println("Test Message ${msg.smsMsg}")
            assertEquals(msg.accNumber, msgParser.getAccountNumber(msg.smsMsg))
        }
    }

    @Test
    fun testPos() {
        val msgParser = MessageParser()
        val sampleMessages = TestConstants.getTestMessageSamples()

        println("Running testPos tests")
        for (msg in sampleMessages) {
            assertEquals(msg.pos, msgParser.getPos(msg.smsMsg))
        }
    }
}