package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StopWords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 测试搜索
 */
public class TestSearchIndex {
    /**
     *  搜索程序入口
     * @param args ：命令行参数
     */
    public static void main(String[] args){
        Sort simpleSorter = new SimpleSorter();
        String indexFile = Config.INDEX_DIR + "index.dat";
        AbstractIndexSearcher searcher = new IndexSearcher();
        searcher.open(indexFile);
        //单个搜索关键词
        AbstractHit[] hits = searcher.search(new Term("coronavirus"), simpleSorter);
        //两个搜索关键词
        //AbstractHit[] hits = searcher.search(new Term("activity"), new Term("fizzy"), simpleSorter, AbstractIndexSearcher.LogicalCombination.OR);
        //包含两个单词的短语检索
       //AbstractHit[] hits = searcher.search(new Term("activity"), new Term("fizzy"), simpleSorter, AbstractIndexSearcher.LogicalCombination.AND);
        for(AbstractHit hit : hits) {
            System.out.println(hit);
        }
    }
}
