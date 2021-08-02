package itempool

import kotlin.math.max

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

    /**
     * 方法一： 动态规划
     * 对于一个子串而言，如果它是回文串，并且长度大于2，那么将它首尾的两个字母去除之后，它仍然是个回文串
     *
     * 用 P(i,j) 表示字符串s的第i到j个字母组成的串（下文表示成s[i:j]）是否为回文串：
     *      P(i, j) = P(i+1, j-1)^(Si == Sj)
     *  也就是说，只有 s[i+1:j−1] 是回文串，并且s的第i和j个字母相同时，s[i:j] 才会是回文串。
     *
     *  false情况：1. s[i, j]本事不是一个回文串；2. i>j, 此时s[i, j]不合法
     *
     *  上文的所有讨论是建立在子串长度大于2的前提之上的，还需要考虑动态规划中的边界条件，即子串的长度为1或2:
     *      P(i, i) = true              // 对于长度为 11 的子串，它显然是个回文串
     *      P(i, i+1) = (Si == Si+1)    // 对于长度为 22 的子串，只要它的两个字母相同，它就是一个回文串
     */
    fun longestPalindromeDynamicProgram(s: String): String {
        val len = s.length
        if (len <= 2) return s

        var begin = 0
        var maxLen = 0
        // dp[i][j] 表示 s[i..j] 是否是回文串
        val dp = Array(len){BooleanArray(len)}

        // 初始化：所有长度为1的子串都是回文串
        for (i in 0 until len) {
            dp[i][i] = true
        }

        // 先从长度开始递推，因为长度为1的情况已经预设了true的条件，所以我们可以从长度为2的情况还是扩展。
        // 注意，这里的长度是可以到len的，即判定整个s是否是回文串
        // for (L in 2 until len) {
        for (L in 2..len) {
            // 枚举从最左边开始，一直到最右边。这里的边界条件就是右边坐标超过字符串的边界
            for (i in 0 until len) {
                // 确定右边界的坐标， L = j-i+1
                val j = L+i-1
                // 如果右边界越界，就可以退出当前循环
                // WRONG: java.lang.StringIndexOutOfBoundsException
                // if (j > len) break
                if (j >= len) break

                if (s[i] != s[j]) {
                    // 如果最外边的两个字母不相等，一定不是回文串
                    dp[i][j] = false
                } else {
                    if (j - i < 3) {
                        // 长度为2和3时，只要字母相等就能说明是回文串
                        dp[i][j] = true
                    } else {
                        // 两边字母相等，是否是回文串，看子串是否是回文串
                        dp[i][j] = dp[i+1][j-1]
                    }
                }

                // 注意，每次判断完一组回文串后都要坚持当前的情况：
                // 如果是回文串，并且长度大于当前最大值 => 替换
                if (dp[i][j] && maxLen < j - i + 1) {
                    begin = i
                    maxLen = j - i + 1
                }
            }
        }
        return s.substring(begin, begin + maxLen)
    }


    /**
     * 方法二： 中心扩展算法
     *
     * 因为所有的状态在转移的时候的可能性都是唯一的：
     *  P(i,j) ← P(i+1,j−1) ← P(i+2,j−2) ← ⋯ ← 某一边界情况
     *
     * 所以，我们可以从每一种边界情况开始「扩展」(如果两边的字母相同，我们就可以继续扩展)，也可以得出所有的状态对应的答案，
     * 其中边界情况即为子串长度为 1 或 2 的情况
     *
     * 方法二的本质即为：我们枚举所有的「回文中心」并尝试「扩展」，直到无法扩展为止，此时的回文串长度即为此
     * 「回文中心」下的最长回文串长度。我们对所有的长度求出最大值，即可得到最终的答案。
     *
     */
    fun longestPalindromeCenterExtension(s: String): String {
        if (s.isBlank()) return ""

        // 初始化最大回文子串的起点和终点
        var begin = 0
        var maxLen = 1

        // 遍历每个位置，当做中心位
        for (i in s.indices) {
            // 奇数中心的扩散长度
            val len1 = expandAroundCenter(s, i, i)
            // 偶数中心的扩散长度
            val len2 = expandAroundCenter(s, i, i+1)
            val len = max(len1, len2)
            if (len > maxLen) {
                // 根据i和maxLen算begin下标
                // 奇数：i-maxLen/2
                // 偶数：i-maxLen/2+1
                // 统一：i-(maxLen-1)/2
                begin = i - (len - 1) / 2
                maxLen = len
            }
        }
        return s.substring(begin, begin + maxLen)
    }

    private fun expandAroundCenter(s: String, l: Int, r: Int): Int {
        var left = l
        var right = r
        // left = right 的时候，此时回文中心是一个字符，回文串的长度是奇数
        // right = left + 1 的时候，此时回文中心是一个空隙，回文串的长度是偶数
        if (left > 0 && right < s.length && s[left] == s[right]) {
            left -= 1
            right += 1
        }
        // 循环跳出：cs[i]!=cs[j],如abc,cs[i]=a,cs[j]=c,回文中心长度为1
        // 此时的回文中心长度：j-i+1-2=j-i-1
        return right - left - 1
    }


    fun longestPalindromeErica(s: String): String {
        if (s.isBlank()) {
            return s
        }
        // 示例 3
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

//        val ret = LongestPalindrome5.longestPalindromeErica(input)
        val ret = LongestPalindrome5.longestPalindromeDynamicProgram(input)
//        val ret = LongestPalindrome5.longestPalindromeCenterExtension(input)

        println("The longest palindrome is: $ret")
    }
}