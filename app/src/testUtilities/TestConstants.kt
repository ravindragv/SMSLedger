package io.github.ravindragv.smsledger

import io.github.ravindragv.smsledger.data.Transaction

object TestConstants {
    fun getTestMessageSamples(): List<Transaction> {
        val sampleMessages: MutableList<Transaction> = mutableListOf()
        val sentBy = "SampleBank"
        val timeStampOne = 1628788569000L
        val timeStampTwo = 1628792046000L

        sampleMessages.add(
            Transaction("Transaction Alert: INR 153.25 has been spent on your YES BANK " +
                    "Credit Card ending with 1111 at AMAZON PAY INDIA PRIVA on 11-08-2021 at " +
                    "07:45:53 pm. Avl Bal INR 123,456.59. In case of suspicious transaction, to " +
                    "block your card, SMS BLKCC {Space}{Last 4 digits of card number} to " +
                    "9840909000 from your registered mobile number.",
                sentBy,
                timeStampOne,
                MessageParser.TransactionType.DEBIT,
                MessageParser.AccountType.CREDIT_CARD,
                153.25f,
                1111,
                "AMAZON PAY INDIA PRIVA")
        )

        sampleMessages.add(
            Transaction("Dear Cardmember, payment of Rs.123,456.78 credited towards your " +
                    "YES BANK Credit Card ending 1111 through NEFT on 11/02/21",
                sentBy,
                timeStampTwo,
                MessageParser.TransactionType.CREDIT,
                MessageParser.AccountType.CREDIT_CARD,
                123456.78f,
                1111,
                "")
        )

        sampleMessages.add(
            Transaction("Thanks for using HDFC Bank Visa Giftcard III Y Card XXXX2222 " +
                    "for INR 35 at Dunzo Digital PVT Lim on 23-APR-21 09:04 AM. Card Bal: " +
                    "INR 9916. Not You ? Call 912261606161.",
                sentBy,
                timeStampOne,
                MessageParser.TransactionType.DEBIT,
                MessageParser.AccountType.DEBIT_CARD,
                35f,
                2222,
                "Dunzo Digital PVT Lim")
        )

        sampleMessages.add(
            Transaction("Dear Customer, INR 437.77 is debited on ICICI Bank Credit " +
                    "Card XX3333 on 08-Aug-21. Info: Amazon. Available Limit: INR 76,544.23. " +
                    "Call on 18002662 for dispute or SMS BLOCK 4005 to 9215676766.",
                sentBy,
                timeStampTwo,
                MessageParser.TransactionType.DEBIT,
                MessageParser.AccountType.CREDIT_CARD,
                437.77f,
                3333,
                "Amazon")
        )

        sampleMessages.add(
            Transaction("Acct XX444 debited with INR 7,021.74 on 22-Oct-19.Info: " +
                    "BIL*NEFT*0018.Avbl Bal:INR 1,23,456.30.Call 18002662 for dispute or " +
                    "SMS BLOCK 955 to 9215676766",
                sentBy,
                timeStampOne,
                MessageParser.TransactionType.DEBIT,
                MessageParser.AccountType.ACCOUNT,
                7021.74f,
                444,
                "BIL*NEFT*0018")
        )

        sampleMessages.add(
            Transaction("Dear Customer, your Account XX555 has been credited with " +
                    "INR 10,000.00 on 21-Nov-19. Info: NEFT-112126839GN00131-LIBERT. Available " +
                    "Balance: INR 3,45,678.29",
                sentBy,
                timeStampTwo,
                MessageParser.TransactionType.CREDIT,
                MessageParser.AccountType.ACCOUNT,
                10000f,
                555,
                "NEFT-112126839GN00131-LIBERT")
        )

        sampleMessages.add(
            Transaction("Dear Customer, your Account XX666 has been credited with " +
                    "INR 2,34,567.00 on 30-Mar-20. Info: NEFT-DOR1436664 0034-PAYPAL. " +
                    "Available Balance: INR 4,56,789.20",
                sentBy,
                timeStampOne,
                MessageParser.TransactionType.CREDIT,
                MessageParser.AccountType.ACCOUNT,
                234567.00f,
                666,
                "NEFT-DOR1436664 0034-PAYPAL")
        )

        sampleMessages.add(
            Transaction("Your VPA batman@wayne linked to Indian Bank a/c no. " +
                    "XXXXX7777 is debited for Rs.546.00 and credited to robin@wayne " +
                    "(UPI Ref no 116812367817).-Indian Bank",
                sentBy,
                timeStampTwo,
                MessageParser.TransactionType.DEBIT,
                MessageParser.AccountType.ACCOUNT,
                546.00f,
                7777,
                "robin@wayne")
        )

        sampleMessages.add(
            Transaction("Dear customer, transaction number TW0234567891 for Rs2299.00 by " +
                    "SBI Debit Card X8888 at AMAZON on 17Aug21 at 17:40:30. If not done forward " +
                    "this SMS to 9223008333 or call 18001111109/9449112211 to block card",
                sentBy,
                timeStampOne,
                MessageParser.TransactionType.DEBIT,
                MessageParser.AccountType.DEBIT_CARD,
                2299f,
                8888,
                "AMAZON")
        )

        sampleMessages.add(
            Transaction("Your HSBC credit card xxxxx9999 is used at amazon for INR " +
                    "1568.00 on 06/08/21. Available limit - INR 124177.52; outstanding - " +
                    "INR 25822.48. If you suspect this is fraudulent, call +914061268002 Or " +
                    "SMS  BLOCK<space>CC Last 4 digit of your card to 575750 from your " +
                    "registered number.",
                sentBy,
                timeStampTwo,
                MessageParser.TransactionType.DEBIT,
                MessageParser.AccountType.CREDIT_CARD,
                1568f,
                9999,
                "amazon")
        )

        return sampleMessages
    }
}