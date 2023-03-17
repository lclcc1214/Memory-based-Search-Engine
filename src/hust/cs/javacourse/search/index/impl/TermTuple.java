package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.AbstractTermTuple;

public class TermTuple extends AbstractTermTuple {

    public TermTuple(){}
    public TermTuple(AbstractTerm term, int curPos){
        this.term = term;
        this.curPos = curPos;
    }

    /**
     * 判断二个三元组内容是否相同
     * @param obj ：要比较的另外一个三元组
     * @return 如果内容相等（三个属性内容都相等）返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(this == null || this.getClass() != obj.getClass()) return false;
        TermTuple termTuple = (TermTuple) obj;
        return curPos == termTuple.curPos && term.equals(termTuple.term);
    }

    /**
     * 获得三元组的字符串表示
     * @return ： 三元组的字符串表示
     */
    @Override
    public String toString() {
        return "TermTuple{" +
                "Term: " + term +
                ",CurPos: " + curPos +
                ",Freq: " + freq +
                "}";
    }
}
