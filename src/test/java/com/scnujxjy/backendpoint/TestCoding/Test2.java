package com.scnujxjy.backendpoint.TestCoding;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class Test2 {
    /**
     * 一个机器人位于一个 m xn 网格的左上角 (起始点在下图中标记为“Start” )。机器人每次只能向下或者向右移动一步。机器人试图达到网格的右下角(在下图中标记为"Finish”)
     * 问总共有多少条不同的路径?
     *
     *
     * 例如，上图是一个7 x 3 的网格。有多少可能的路径?
     * 说明: m和n的值均不超过 100
     *
     * 输入:m=3,n = 2
     * 输出:3
     * 解释:
     * 从左上角开始，总共有 3 条路径可以到达右下角
     * 1.向右 ->向右 >向下
     * 2.向右 ->向下 ->向右
     * 3.向下 ->向右 ->向右
     */

    @Test
    public void test(){
        int m = 3, n = 2;
        int paths = uniquePaths(m, n);
        System.out.println("可能的路径为 " + paths);
    }

    /**
     *
     * @param m 网格行数
     * @param n 网格列数
     * @return 可能的 路径数
     */
    public int uniquePaths(int m, int n){

        if(m <= 0 || n <= 0){
            throw new IllegalArgumentException("输入参数错误，m 和 n 必须大于 0");
        }

        List<List<Integer>> inputList = new ArrayList<>();

        for(int i = 0; i< m; i++){
            List<Integer> list1 = new ArrayList<>();
            for(int j = 0; j < n; j++){
                list1.add(0);
            }
            inputList.add(list1);
        }

        System.out.println(inputList);

        for(int i = 0; i < m; i ++){
            for(int  j = 0; j < n; j++){
                if(i == 0 && j == 0){
                    inputList.get(i).set(j, 1);
                }else if(i == 0){
                    inputList.get(i).set(j, 1);
                }else if(j == 0){
                    inputList.get(i).set(j, 1);
                }else{
                    inputList.get(i).set(j, inputList.get(i).get(j-1) + inputList.get(i-1).get(j));
                }
            }
        }


        return inputList.get(m-1).get(n-1);
    }
}
