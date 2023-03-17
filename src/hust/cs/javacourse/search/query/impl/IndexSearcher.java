package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.min;

public class IndexSearcher extends AbstractIndexSearcher {

    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     *
     * @param indexFile ：指定索引文件
     */
    @Override
    public void open(String indexFile) {
        index.load(new File(indexFile));
        index.optimize();
    }

    /**
     * 根据单个检索词进行搜索
     *
     * @param queryTerm ：检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        // 调用index的search方法，检索单词queryTerm，结果赋给消息列表postingList。
        // 判断postingList是否为空，若为空，则返回一个新建命中结果数组AbstractHit[0]；
        // 若不为空，则构建一个命中结果列表hitList，用来存储命中结果。
        AbstractPostingList postingList = index.search(queryTerm);
        if (postingList == null) return new AbstractHit[0];
        List<AbstractHit> hitList = new ArrayList<>();

        // 调用sort方法，对postingList进行排序。
        postingList.sort();

        //循环遍历postingList中的每一个posting，
        // 对posting调用getDocId方法，得到文档的编号docId；
        // 调用index的getDocName方法，得到文档的绝对路径docPath；
        // 再以docId和docPath为参数，构建一个命中结果hit；
        // 将检索词和posting放入hit的TermPostingMapping里；
        // 使用排序器sorter的score方法，对命中结果hit进行评分；
        // 再将hit加入到hitList中。

        for (int i = 0; i < postingList.size(); i++) {
            AbstractPosting posting = postingList.get(i);
            String docPath = index.getDocName(posting.getDocId());
            AbstractHit hit = new Hit(posting.getDocId(), docPath);
            hit.getTermPostingMapping().put(queryTerm, posting);
            sorter.score(hit);
            hitList.add(hit);
        }

        // 调用排序器sorter的sort方法，对命中结果列表hitList进行排序。
        // 创建命中数组ret，大小为hitList的size。
        // 最后将hitList数组化之后的结果赋给ret，并返回ret。
        sorter.sort(hitList);
        AbstractHit[] ret = new AbstractHit[hitList.size()];
        return hitList.toArray(ret);
    }


    /**
     * 根据二个检索词进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @param combine    ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {
        // 调用index的search方法，检索单词queryTerm1，queryTerm2，结果赋给消息列表postingList1，postingList2。
        // 若postingList1、postingList2不为空，则对其调用sort方法进行排序。
        AbstractPostingList postingList1 = index.search(queryTerm1);
        AbstractPostingList postingList2 = index.search(queryTerm2);
        if(postingList1 != null){
            postingList1.sort();
        }
        if(postingList2 != null){
            postingList2.sort();
        }
        // 创建命中列表hitArrayList，用来存储命中结果。
        ArrayList<AbstractHit> hitArrayList = new ArrayList<>();

        // 若逻辑联系词为逻辑与AND
        if(combine == LogicalCombination.AND){

            // 若postingList1或者postingList2为空，则一定没有命中结果，
            // 返回一个新建命中结果数组AbstractHit[0]。
            if(postingList1 == null || postingList2 == null){
                return new AbstractHit[0];
            }

            // 遍历postingList1，postingList2，
            // 其中i为postingList1当前遍历下标，j为postingList2当前遍历下标，
            // 当i小于postingList1的长度并且j小于postingList2的长度时，则说明尚未遍历完可能的结果，
            // 令ii为postingList1中下标为i的posting，jj为postingList2中下标为j的posting
            // 调用posting的compareTo方法，
            // 若ii小于jj，则对i+1；若ii大于jj，则对j+1；
            // 只有当ii等于jj时，创建命中结果hit，并记录进命中结果列表hitArrayList中，再将i和j都加一。
            int i = 0, j = 0;
            while(i < postingList1.size() && j < postingList2.size()){
                AbstractPosting ii = postingList1.get(i);
                AbstractPosting jj = postingList2.get(i);
                if(ii.compareTo(jj) < 0){
                    i++;
                } else if(ii.compareTo(jj) > 0){
                    j++;
                } else{
                    AbstractHit hit = new Hit(ii.getDocId(), index.getDocName(ii.getDocId()));
                    hit.getTermPostingMapping().put(queryTerm1, ii);
                    hit.getTermPostingMapping().put(queryTerm2, jj);
                    sorter.score(hit);
                    hitArrayList.add(hit);
                    i++;
                    j++;
                }
            }
        } else {
            // 若逻辑联系词为逻辑与OR

            // 若postingList1或者postingList2为空，则一定没有命中结果，
            // 返回一个新建命中结果数组AbstractHit[0]。
            if(postingList1 == null || postingList2 == null){
                return new AbstractHit[0];
            }

            // 遍历postingList1，postingList2，
            // 其中i为postingList1当前遍历下标，j为postingList2当前遍历下标，
            // size1为postingList1的长度，size2为postingList2的长度；
            // 当i小于postingList1的长度或者j小于postingList2的长度时，则说明尚未遍历完可能的结果，
            // 若i小于size1，则令ii为postingList1中下标为i的posting，否则为null，
            // 若j小于size2，则令jj为postingList2中下标为j的posting，否则为null。
            // 当ii，jj不为空时，记录较小的docId
            // 根据当前记录的最小的docId，调用index的getDocName方法，得到对应文档的名字docName
            // 再以docId和docName，创建命中结果hit，
            // 根据id为ii还是jj，将对应单词和posting加入TermPostingMapping
            // 最后将命中结果hit加入命中结果列表hitArrayList中
            int i = 0, j = 0, size1 = postingList1 == null ? 0 : postingList1.size(), size2 = postingList2 == null ? 0 : postingList2.size();
            while(i < size1 || j < size2){
                AbstractPosting ii = i < size1 ? postingList1.get(i) : null;
                AbstractPosting jj = j < size2 ? postingList2.get(j) : null;
                int id = 0x3f3f3f3f;
                if(ii != null){
                    id = min(id, ii.getDocId());
                }
                if(jj != null){
                    id = min(id, jj.getDocId());
                }
                AbstractHit hit = new Hit(id, index.getDocName(id));
                if(ii != null){
                    if(ii.getDocId() < id){
                        i++;
                    } else if(ii.getDocId() == id){
                        i++;
                        hit.getTermPostingMapping().put(queryTerm1, ii);
                    }
                }
                if(jj != null){
                    if(jj.getDocId() < id){
                        j++;
                    } else if(jj.getDocId() == id){
                        j++;
                        hit.getTermPostingMapping().put(queryTerm2, jj);
                    }
                }
                sorter.score(hit);
                hitArrayList.add(hit);
            }
        }
        sorter.sort(hitArrayList);
        AbstractHit[] ret = new AbstractHit[hitArrayList.size()];
        return hitArrayList.toArray(ret);
    }

}
