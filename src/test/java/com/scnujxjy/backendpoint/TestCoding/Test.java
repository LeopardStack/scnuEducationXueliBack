package com.scnujxjy.backendpoint.TestCoding;

import java.util.ArrayList;
import java.util.List;

public class Test {
    /**
     * 给出一组数字，返回该组数字的所有排列
     * 例如：
     * [1,2,3]的所有排列如下
     * [1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2], [3,2,1]
     *
     * 1 不动  2 3 开始
     * 2 不动 1 3 开始
     *  [1, 2, 3, 4]
     *  [1, 2]  [3, 4]
     *  [1, 2, 3]  [4]
     *  [1, 2, 4]  [3]
     *  [1, 2, 3, 4]  []
     *  【1， 2， 4， 3】
     */
    @org.junit.jupiter.api.Test
    public void test1(){
        List<Integer> input = new ArrayList<>();
        input.add(1);
        input.add(2);
        input.add(3);
//        input.add(1);
        List<List<Integer>> allCs = getAllCs(input);
        int count = 0;
        for(List<Integer> list1: allCs){
            System.out.print("[");
            for(Integer i : list1){
                System.out.print(i + ",");
            }
            if(count == (allCs.size()-1)){
                System.out.print("]");
            }else{
                System.out.print("],");
            }

        }
    }

    public List<List<Integer>> getAllCs(List<Integer> input){
        if(input.isEmpty()){
            throw new IllegalArgumentException("输入参数为空");
        }
        List<Integer> staticList = new ArrayList<>();
        int staticSize = 1;
        staticList = input.subList(0, staticSize);
        List<Integer> dynamicList = new ArrayList<>();
        dynamicList = input.subList(staticSize, input.size());
        System.out.println(staticList);
        System.out.println(dynamicList);
        List<List<Integer>> outputList = new ArrayList<>();
        aFunc(staticList, dynamicList, input.subList(1, input.size()), input.size(), 0, outputList);
        return outputList;
    }

    public List<Integer> aFunc(List<Integer> staticList, List<Integer> dynamicList, List<Integer> newdynamicList, int totalSize, int start, List<List<Integer>> outputList){
        if(staticList.size() == totalSize){
            System.out.println(staticList);
            outputList.add(staticList);
            return staticList;
        }
        List<Integer> newDynamicList = new ArrayList<>();
        for(int j = 0; j < dynamicList.size(); j++){
            if(j != start){
                newDynamicList.add(dynamicList.get(j));
            }

        }
        List<Integer> newStaticList = new ArrayList<>();
        for(Integer ite : staticList){
            newStaticList.add(ite);
        }
        newStaticList.add(dynamicList.get(start));
        List<Integer> list = aFunc(newStaticList, dynamicList, newDynamicList, totalSize, start + 1, outputList);
        while (start <= dynamicList.size()){
            start = start + 1;
            aFunc(newStaticList, dynamicList, newDynamicList, totalSize, start, outputList);
        }
        return list;
    }
}
