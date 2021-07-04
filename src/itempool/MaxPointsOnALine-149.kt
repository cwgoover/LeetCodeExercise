package itempool

import itempool.MaxPointsOnALine149.maxPoints
import java.lang.Integer.max
import kotlin.math.abs

/**
 * 给你一个数组 points ，其中 points[i] = [xi, yi] 表示 X-Y 平面上的一个点
 * 求最多有多少个点在同一条直线上
 * 提示：
 *      1 <= points.length <= 300
 *      points[i].length == 2
 *      -10^4 <= xi, yi <= 10^4
 *      points 中的所有点 互不相同
 *
 * https://leetcode-cn.com/problems/max-points-on-a-line/
 * Created by i352072(erica.cao@sap.com) on 06/29/2021
 */
object MaxPointsOnALine149 {

    fun maxPoints(points: Array<IntArray>): Int {
        // 优化1：当点总数量 <= 2 时，总有一条直线将所有点串联，所以直接返回点的总数量即可
        val n = points.size
        if (n <= 2) return n

        // 直线公式: y = (y2-y1)/(x2-x1)x + (x2y1-x1y2)/(x2-x1) = kx + b
        // 斜率 slope = (y2-y1)/(x2-x1) = my/mx
        // 判断在一条直线上：即两个点算下来的(k, b)要和其他任意两个点(也可能包含其中任意一个点)的(k, b)一样
        // 如果随机遍历数组上所有点算两个点的(k, b)过于复杂；所以在遍历时，我们可以从数组头开始选定第一个起始点，然后遍历这个起始点
        // 和它后面的任意一个点组成直线的斜率。因为起始点一致，所以我们只需要确定k也就是他们的斜率一致就可以说明两点在一条直线上
        // 这样在数组上，从头开始一个点遍历一遍，然后移到下一个点再遍历一遍，找出斜率相同数最多的线就是maxPoints ！！
        // 这样下来，如果移动到后面的点再遍历中发现它和某点的斜率跟前面的点遍历中发现的斜率相同，那必定两个线是平行的。
        // 因为如果两线是同一条直线的话，那这三个点在一条直线上的话，在遍历最前面那个点时必定被发现

        // 需要注意的是，因为浮点数类型可能因为精度不够而无法足够精确地表示每一个斜率，因此我们需要换一种方法来记录斜率, 用分子和分母组成的二元组来代表斜率
        // 这样就需要考虑以下三种情况：
        // 1. 1/2 = 2/4, 虽然二元组不同，但是实际slope相同，需要化简为最简分数形式 => 将分子和分母同时除以二者绝对值的最大公约数
        //               mx = (x2-x1)/gcd(abs(x2-x1), abs(y2-y1)), my = (y2-y1)/gcd(abs(x2-x1), abs(y2-y1)) => 二元组(mx, my)
        // 2. 分子、分母为0的情况。因为题目中不存在重复的点，因此不存在两数均为 0 的情况; 但若有一方为0，此时两数不存在数学意义上的最大公约数，需特殊处理
        //               当mx为0，令my=1; 当my为0，令mx=1
        // 3. 考虑负数情况，-1/2 = 1/-2; 规定分子为非负整数，即如果 my 为负数，我们将二元组中两个数同时取相反数即可

        // 经过上述操作之后，即可得到最终的二元组 (mx,my)
        // 在本题中，因为点的横纵坐标取值范围均为 [-10^4, 10^4]，所以斜率 slope= my/mx中，
        // mx 落在区间 [-2*10^4, 2*10^4]内，my(分子) 落在区间 [0, 2*10^4]
        // 具体地，我们令 val = my + (2*10^4 + 1)*mx, 即用一个数val来表示(mx, my). 因为my的最大边界为2*10^4，
        // +1后值已经超出my的范围在乘以mx，相当于把mx在二进制状态下向右移动到my表示位的右侧，用后面几位来表示mx

        var ret = 0
        for (i in 0 until n) {
            if (ret >= n - i || ret > n / 2) {
                // 优化2:
                // 1. 当遍历到i（假设编号从 00 开始）时，至多再能找到 length-i 个点共线。如果此时ret大于 length-i 即可停止遍历，因不可能再找到更大的答案了
                // 2. 当找到一条直线经过超过半数的点时，即可确定该直线为经过最多点的直线
                break
            }
            val map: HashMap<Int, Int> = hashMapOf()
            for (j in (i + 1) until n) {
                var x = points[i][0] - points[j][0]
                var y = points[i][1] - points[j][1]
                when {
                    x == 0 -> {
                        y = 1
                    }
                    y == 0 -> {
                        x = 1
                    }
                    // WRONG!!
//                    y < 0 -> {
//                        x = -x
//                        y = -y
//                    }
                    else -> {
                        if (y < 0) {
                            x = -x
                            y = -y
                        }
                        // WRONG: 这里不能这样用，否则第一个执行完后x已经发生变化了，y就不准了
//                        x /= gcd(abs(x), abs(y))    // x = x / gcd(abs(x), abs(y))
//                        y /= gcd(abs(x), abs(y))
                        val gcdXY = gcd(abs(x), abs(y))
                        x /= gcdXY
                        y /= gcdXY
                    }
                }
                val variantSlope = y + 20001 * x
                // Save the number of occurrences
                map[variantSlope] = map.getOrDefault(variantSlope, 0) + 1
            }
            var maxSlopes = 0
            for (key in map.keys) {
                map[key]?.let {
//                    if (maxSlopes < it) {
//                        maxSlopes = it
//                    }
                    // 其经过的点数为该斜率出现的次数加一（点 i 自身也要被统计）!!
                    maxSlopes = max(maxSlopes, it + 1)
                }
            }
//            if (ret < maxSlopes) {
//                ret = maxSlopes
//            }
            ret = max(ret, maxSlopes)
        }
        return ret
    }

    // 最大公约数, GCD: Greatest Common Divisor; the same as HCF: Highest Common Factor
    private tailrec fun gcd(a: Int, b: Int): Int = if (b == 0) abs(a) else gcd(b, a % b)

}

fun main() {
    // A 6x5 array of Int, all set to 0.
//    var m = Array(6) {Array(5) {0} }

    println("points = [[1,1],[2,2],[3,3]]")
    val point1: IntArray = intArrayOf(1, 1)
    val point2: IntArray = intArrayOf(2, 2)
    val point3: IntArray = intArrayOf(3, 3)
    val points: Array<IntArray> = arrayOf(point1, point2, point3)
    val maxPoints = maxPoints(points)
    println("the max points on a line: $maxPoints")

    println()
    println("points = [[1,1],[3,2],[5,3],[4,1],[2,3],[1,4]]")
    val p1: IntArray = intArrayOf(1, 1)
    val p2: IntArray = intArrayOf(3, 2)
    val p3: IntArray = intArrayOf(5, 3)
    val p4: IntArray = intArrayOf(4, 1)
    val p5: IntArray = intArrayOf(2, 3)
    val p6: IntArray = intArrayOf(1, 4)
    val ps: Array<IntArray> = arrayOf(p1, p2, p3, p4, p5, p6)
    val mp = maxPoints(ps)
    println("the max points on a line: $mp")
}