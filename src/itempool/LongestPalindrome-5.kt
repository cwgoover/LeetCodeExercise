package itempool

/**
 * 最长回文子串
 * 给你一个字符串 s，找到 s 中最长的回文子串。
 * 示例 1：
 *  输入：s = "babad"
 *  输出："bab"
 *  解释："aba" 同样是符合题意的答案。
 *
 * 示例 2：
 *  输入：s = "cbbd"
 *  输出："bb"
 *
 * 示例 3：
 *  输入：s = "a"
 *  输出："a"
 *
 * 示例 4：
 *  输入：s = "ac"
 *  输出："a"
 *
 * 提示：
 *  1 <= s.length <= 1000
 *  s 仅由数字和英文字母（大写和/或小写）组成
 *
 *  https://leetcode-cn.com/problems/longest-palindromic-substring/
 * Created by i352072(erica.cao@sap.com) on 06/30/2021
 */
object LongestPalindrome5 {

    fun longestPalindrome(s: String): String {
        if (s.isBlank()) {
            return s
        }
        val arr = s.toList()
        if (arr.size == 1) {
            return s
        }
        val ret = mutableListOf<Char>()
        for (i in arr.indices) {
            val tmp = mutableListOf<Char>()
            tmp.add(arr[i])
            for (j in i + 1 until arr.size) {
                tmp.add(arr[j])
                // 如果tmp不是回文字进入下一个循环(tmp加一个char继续判断)，如果是赋值给ret
                var isPal = true
                for (k in 0..(tmp.size / 2)) {
                    if (tmp[k] != tmp[tmp.size-k-1]) {
                        isPal = false
                        break
                    }
                }
                if (isPal && ret.size < tmp.size) {
                    ret.clear()
                    ret.addAll(tmp)
                }
            }
        }
        // 示例4
        if (ret.size == 0) {
            ret.add(arr[0])
        }
        return ret.joinToString("")
    }
}

fun main() {
    while (true) {
        println("Enter string (enter nothing to exit):")
        val input = readLine() ?: break // TODO: doesn' work
        val ret = LongestPalindrome5.longestPalindrome(input)
        println("The longest palindrome is: $ret")
    }
}