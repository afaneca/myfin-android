package com.afaneca.myfin.utils

import android.content.Context
import com.afaneca.myfin.R
import com.afaneca.myfin.base.objects.MyFinAccount
import com.afaneca.myfin.data.db.accounts.UserAccountEntity

/**
 * Created by me on 19/06/2021
 */
object MyFinUtils {

    /**
     * Returns a sublist of the [originalList] containing only operating funds accounts
     */
    fun getOnlyOperatingFundsAccountsFromDBFullList(originalList: List<UserAccountEntity>): List<UserAccountEntity> =
        originalList.filter { acc ->
            !acc.type.isNullOrBlank() && when (acc.type) {
                MyFinConstants.ACCOUNT_TYPE.CHECKING.value,
                MyFinConstants.ACCOUNT_TYPE.SAVINGS.value,
                MyFinConstants.ACCOUNT_TYPE.MEAL.value,
                MyFinConstants.ACCOUNT_TYPE.WALLET.value
                -> true
                else -> false
            }
        }


    /**
     * Returns a sublist of the [originalList] containing only investing accounts
     */
    fun getOnlyInvestingAccountsFromDBFullList(originalList: List<UserAccountEntity>): List<UserAccountEntity> =
        originalList.filter { acc ->
            !acc.type.isNullOrBlank() && when (acc.type) {
                MyFinConstants.ACCOUNT_TYPE.INVESTING.value,
                -> true
                else -> false
            }
        }

    /**
     * Returns a sublist of the [originalList] containing only credit accounts
     */
    fun getOnlyCreditAccountsFromDBFullList(originalList: List<UserAccountEntity>): List<UserAccountEntity> =
        originalList.filter { acc ->
            !acc.type.isNullOrBlank() && when (acc.type) {
                MyFinConstants.ACCOUNT_TYPE.CREDIT.value,
                -> true
                else -> false
            }
        }

    /**
     * Returns a sublist of the [originalList] containing only operating funds accounts
     */
    fun getOnlyOperatingFundsAccountsFromFullList(originalList: List<MyFinAccount>): List<MyFinAccount> =
        originalList.filter { acc ->
            !acc.type.isNullOrBlank() && when (acc.type) {
                MyFinConstants.ACCOUNT_TYPE.CHECKING.value,
                MyFinConstants.ACCOUNT_TYPE.SAVINGS.value,
                MyFinConstants.ACCOUNT_TYPE.MEAL.value,
                MyFinConstants.ACCOUNT_TYPE.WALLET.value
                -> true
                else -> false
            }
        }


    /**
     * Returns a sublist of the [originalList] containing only investing accounts
     */
    fun getOnlyInvestingAccountsFromFullList(originalList: List<MyFinAccount>): List<MyFinAccount> =
        originalList.filter { acc ->
            !acc.type.isNullOrBlank() && when (acc.type) {
                MyFinConstants.ACCOUNT_TYPE.INVESTING.value,
                -> true
                else -> false
            }
        }

    /**
     * Returns a sublist of the [originalList] containing only credit accounts
     */
    fun getOnlyCreditAccountsFromFullList(originalList: List<MyFinAccount>): List<MyFinAccount> =
        originalList.filter { acc ->
            !acc.type.isNullOrBlank() && when (acc.type) {
                MyFinConstants.ACCOUNT_TYPE.CREDIT.value,
                -> true
                else -> false
            }
        }

    /**
     * Returns a sublist of the [originalList] containing only other accounts
     */
    fun getOnlyOtherAccountsFromFullList(originalList: List<MyFinAccount>): List<MyFinAccount> =
        originalList.filter { acc ->
            !acc.type.isNullOrBlank() && when (acc.type) {
                MyFinConstants.ACCOUNT_TYPE.OTHER.value,
                -> true
                else -> false
            }
        }

    /**
     * Returns a readable (and localized, hence the need for [context]) string label representing the account type with the specified [tag]
     */
    fun getReadableAccountTypeByTag(context: Context, tag: String): String = context.getString(
        when (tag) {
            MyFinConstants.ACCOUNT_TYPE.CHECKING.value -> R.string.account_checking_label
            MyFinConstants.ACCOUNT_TYPE.SAVINGS.value -> R.string.account_savings_label
            MyFinConstants.ACCOUNT_TYPE.INVESTING.value -> R.string.account_investing_label
            MyFinConstants.ACCOUNT_TYPE.CREDIT.value -> R.string.account_credit_label
            MyFinConstants.ACCOUNT_TYPE.MEAL.value -> R.string.account_meal_card_label
            MyFinConstants.ACCOUNT_TYPE.WALLET.value -> R.string.account_wallet_label
            else -> R.string.account_other_label
        }
    )
}
