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
            //println("Test Message ${msg.msgBody}")
            assertEquals(msg.transactionType, msgParser.getTransactionType(msg.msgBody))
        }
    }

    @Test
    fun testTransactionAmount() {
        val msgParser = MessageParser()
        val sampleMessages = TestConstants.getTestMessageSamples()

        println("Running testTransactionAmount tests")
        for (msg in sampleMessages) {
            //println("Test Message ${msg.msgBody}")
            assertEquals(msg.transactionAmount, msgParser.getTransactionAmt(msg.msgBody))
        }
    }

    @Test
    fun testAccountType() {
        val msgParser = MessageParser()
        val sampleMessages = TestConstants.getTestMessageSamples()

        println("Running testAccountType tests")
        for (msg in sampleMessages) {
            //println("Test Message ${msg.msgBody}")
            assertEquals(msg.accountType, msgParser.getAccountType(msg.msgBody))
        }
    }
}