package com.szr.co.smart.qr.bill.position

import com.szr.co.smart.qr.bill.type.ViBillType

enum class ViBillPosition(
    val position: String,
    val type: ViBillType) {

    POS_OPEN("sq_start", ViBillType.TYPE_OPEN),
    POS_LAN_INTERS("sq_lng_i", ViBillType.TYPE_INTERSTITIAL),
    POS_LAN_NATIVE("sq_lng_n", ViBillType.TYPE_NATIVE),

    POS_RETURN_INTERS("sq_back_i", ViBillType.TYPE_INTERSTITIAL),
    POS_MAIN_CLICK_INTERS("sq_m_clk_i", ViBillType.TYPE_INTERSTITIAL),
    POS_MAIN_NATIVE("sq_m_n", ViBillType.TYPE_NATIVE),

    POS_HISTORY_NATIVE("sq_history_n", ViBillType.TYPE_NATIVE),
    POS_QR_CREATE_NATIVE("sq_qrc_n", ViBillType.TYPE_NATIVE),
    POS_QR_CREATE_CLICK_INTERS("sq_qrc_clk_i", ViBillType.TYPE_INTERSTITIAL),
    POS_QR_RESULT_NATIVE("sq_qr_result_n", ViBillType.TYPE_NATIVE),
    POS_QR_CLICK_SAVE_INTERS("sq_qr_clk_save_i", ViBillType.TYPE_INTERSTITIAL),
    POS_QR_SCAN_INTERS("sq_qr_scan_i", ViBillType.TYPE_INTERSTITIAL),

    POS_QR_PARSE_RESULT_INTERS("sq_qr_scan_i", ViBillType.TYPE_INTERSTITIAL),
    POS_QR_PARSE_RESULT_NATIVE("sq_qr_scan_n", ViBillType.TYPE_NATIVE),

    POS_QR_OTHER_NATIVE("sq_other_n", ViBillType.TYPE_NATIVE),
}