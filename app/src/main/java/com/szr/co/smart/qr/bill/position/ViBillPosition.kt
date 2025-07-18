package com.szr.co.smart.qr.bill.position

import com.szr.co.smart.qr.bill.type.ViBillType

enum class ViBillPosition(
    val position: String,
    val type: ViBillType,
    val fillInType: ViBillType? = null
) {

    POS_OPEN("grad_opn", ViBillType.TYPE_OPEN, ViBillType.TYPE_INTERSTITIAL),
    POS_LAN_INTERS("grad_lng_i", ViBillType.TYPE_INTERSTITIAL),
    POS_LAN_NATIVE("grad_lng_n", ViBillType.TYPE_NATIVE),
    POS_TO_USE_INTERS("grad_use_i", ViBillType.TYPE_INTERSTITIAL),
    POS_TO_USE_NATIVE("grad_use_n", ViBillType.TYPE_NATIVE),

    POS_RETURN_INTERS("grad_back_i", ViBillType.TYPE_INTERSTITIAL),
    POS_MAIN_CLICK_INTERS("grad_m_clk_i", ViBillType.TYPE_INTERSTITIAL),
    POS_MAIN_NATIVE("grad_m_n", ViBillType.TYPE_NATIVE),

    POS_AUTHOR_NATIVE("grad_auth_n", ViBillType.TYPE_NATIVE),
    POS_AUTHOR_DETAIL_NATIVE("grad_auth_detail_n", ViBillType.TYPE_NATIVE),

    POS_SAVE_VIDEO_INTERS("grad_saveV_i", ViBillType.TYPE_INTERSTITIAL),

    POS_DOWNLOAD_SUCCESS_NATIVE("grad_dow_su_n", ViBillType.TYPE_NATIVE),

    POS_PARSE_SUC_INTERS("grad_p_su_i", ViBillType.TYPE_INTERSTITIAL),
    POS_PARSE_RESULT_NATIVE("grad_p_re_n", ViBillType.TYPE_NATIVE),
    POS_PARSE_DOWNLOAD_INTERS("grad_pd_i", ViBillType.TYPE_INTERSTITIAL),
}