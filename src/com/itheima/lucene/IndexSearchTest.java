package com.itheima.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

/**
 * Created by 朱梦光 on 2017/9/27.
 */
public class IndexSearchTest {

    @Test
    public void testIndexSearch()throws  Exception{

        //创建分词器(创建索引和所用时的分词器必须一致)
        Analyzer analyzer = new IKAnalyzer();
        //创建查询对象,第一参数:默认搜索域,第二个参数:分词器
        //默认搜索域:如果搜索语法中指定域名,从指定域名中搜索,如果搜索时,只写了查询关键字
        //则从默认搜索域中进行搜索
        QueryParser queryParser = new QueryParser("fileContext",analyzer);
        //查询语法=域名:搜索的关键字
        Query query = queryParser.parse("fileName:jsp");



        //指定索引和文档的位置目录
        Directory dir = FSDirectory.open(new File("E:\\dic"));
        //索引和文档的读取对象

        IndexReader indexReader = IndexReader.open(dir);


        IndexSearcher indexSearcher = new IndexSearcher(indexReader);


        //搜索:第一个参数为查询语句对象
        //第二个参数:指定显示多少条
        TopDocs topDocs = indexSearcher.search(query,5);
        //一共搜索到多少条记录
        System.out.println("==========count=============="+topDocs.totalHits);
        //从搜索结果对象中获取结果集
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (ScoreDoc scoreDoc: scoreDocs) {
            //获取docID
            int docID = scoreDoc.doc;
            //通过文档ID从硬盘中读取出对应的文档
            Document document = indexReader.document(docID);
            //得到域名,可以取出值打印
            System.out.println("fileName:"+document.get("fileName"));
            System.out.println("fileSize:"+document.get("fileSize"));
            System.out.println("=============================");
        }
    }



















}
