package com.siwoo

import kotlin.test.Test
import kotlin.test.assertEquals

class DonutShopTest {

    @Test
    fun testBuyDonut() {
        val credit = CreditCard()
        val purchase = buyDonuts(5, credit)
        assertEquals(purchase.payment.credit, credit)
        assertEquals(purchase.donuts.size, 5)
        assertEquals(purchase.payment.amount, Donut.PRICE * 5)
    }
    
}
