package com.itheima.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 朱梦光 on 2017/9/27.
 */
public class IndexManagerTest {


    @Test
    public void testIndexCreate()throws Exception{
        //创建文档列表,保存多个Document文件
        List<Document> docList = new ArrayList<Document>();
        //采集文件系统中的文档数据放入Lucene中
        //指定文件所在目录
        File dir = new File("C:\\Users\\朱梦光\\Desktop\\高天\\web知识\\后端框架");
        //循环文件夹取出文件
        for (File file: dir.listFiles()) {
            //文件名称
            String fileName = file.getName();
            //文件内容
            String fileContext = FileUtils.readFileToString(file);
            //文件大小
            Long fileSize = FileUtils.sizeOf(file);

            //文档对象,文件系统中的文件就是一个Document对象
            Document doc = new Document();
            //第一个参数:域名
            //第二个参数:域值
            //第三个参数:是否存储,是为yes,不存储为no
            //TextField nameFiled    = new TextField("fileName",fileName, Field.Store.YES);
            //TextField contextFiled = new TextField("fileContext",fileContext, Field.Store.YES);
            //TextField sizeFiled    = new TextField("fileSize",fileSize.toString(), Field.Store.YES);

            //是否分词:需要,因为它需要索引,并且它不是一个整体,分词有意义
            //是否索引:需要,因为要通过它来进行搜索
            //是否存储:需要,因为要直接在页面上显示
            TextField nameFiled    = new TextField("fileName",fileName, Field.Store.YES);
            //是否分词:需要,因为它需要根据内容进行搜索,分词有意义
            //是否索引:需要,因为要通过它来进行搜索
            //是否存储:可以要,也可以不要,不存储内容提取不出来
            TextField contextFiled = new TextField("fileContext",fileContext, Field.Store.NO);
            //是否分词:需要,因为数字要对比,搜索文档的时候可以搜索大小,lucene内部数字进行了分词算法
            //是否索引:要,因为要根据大小进行搜索
            //是否存储:需要,因为需要显示内容大小
           LongField sizeFiled = new LongField("fileSize",fileSize, Field.Store.YES);

            //将所有的域都存到文档中
            doc.add(nameFiled);
            doc.add(contextFiled);
            doc.add(sizeFiled);
            //将文档存入文档集合中
            docList.add(doc);

        }
        //创建分词器,StandardAnalyzer标准分词器,对英文分词效果很好,对中文单字分词
       // Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //指定索引和文档存贮的路径目录
        Directory directory = FSDirectory.open(new File("E:\\dic"));
        //创建写对象的初始化对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);
        //创建索引和文档写对象
        IndexWriter indexWriter = new IndexWriter(directory,config);
        //将文档加入到索引和文档的写对象中
        for (Document doc:docList) {
            indexWriter.addDocument(doc);
        }
        //提交
        indexWriter.commit();
        //关闭流
        indexWriter.close();
    }
    @Test
    public void testIndex()throws Exception{

        //创建分词器,StandardAnalyzer标准分词器,对英文分词效果很好,对中文单字分词
        // Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //指定索引和文档存贮的路径目录
        Directory directory = FSDirectory.open(new File("E:\\dic"));
        //创建写对象的初始化对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);
        //创建索引和文档写对象
        IndexWriter indexWriter = new IndexWriter(directory,config);

        //删除所有
        //indexWriter.deleteAll();
        //根据名称进行删除
        //Term词元,就是一个词,第一个参数:域名,第二个参数:要删除含有此关键字的数据
        indexWriter.deleteDocuments(new Term("fileName","jsp"));
        //提交
        indexWriter.commit();

        indexWriter.close();
    }

    //更新就是按照传入的Term进行搜索,如果找到了结果那么删除,将更新的内容重新生成一个Document对象
    //如果没有搜索到结果,那么将更新的内容直接添加一个新的Document
    @Test
    public void testIndexUpdate() throws Exception{
        //创建分词器,StandardAnalyzer标准分词器,对英文分词效果很好,对中文单字分词
        // Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //指定索引和文档存贮的路径目录
        Directory directory = FSDirectory.open(new File("E:\\dic"));
        //创建写对象的初始化对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);
        //创建索引和文档写对象
        IndexWriter indexWriter = new IndexWriter(directory,config);


        //更新索引
        //根据文件名称进行更新
        Term term = new Term("fileName","EL表达式.txt");
        Document doc = new Document();

        doc.add(new TextField("fileName","xxxxxx", Field.Store.YES));
        doc.add(new TextField("fileContext","think in java xxxx", Field.Store.NO));
        doc.add(new LongField("fileSize",100, Field.Store.YES));
        indexWriter.updateDocument(term,doc);
        //提交
        indexWriter.commit();

        indexWriter.close();

    }
     @Test
     public void testIndexTermQuery() throws Exception{

         //创建分词器(创建索引和所用时的分词器必须一致)
         Analyzer analyzer = new IKAnalyzer();

         //创建词元:就是词
         Term term = new Term("fileName","spring");

         //使用TermQuery查询
         //根据term对象进行查询
         TermQuery termQuery = new TermQuery(term);
         //指定索引和文档的位置目录
         Directory dir = FSDirectory.open(new File("E:\\dic"));
         //索引和文档的读取对象
         IndexReader indexReader = IndexReader.open(dir);
         IndexSearcher indexSearcher = new IndexSearcher(indexReader);

         //搜索:第一个参数为查询语句对象
         //第二个参数:指定显示多少条
         TopDocs topDocs = indexSearcher.search(termQuery,5);
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
    @Test
    public void testNumbericRangeTermQuery() throws Exception{

        //创建分词器(创建索引和所用时的分词器必须一致)
        Analyzer analyzer = new IKAnalyzer();

        //根据数字范围查询进行查询
        //查询文件大小大于2小于7的文章
        //第一个参数:域名
        //第二个参数:最小值
        //第三个参数:最大值
        //第四个参数:是否包含最小值
        //第五个参数:是否包含最大值
        Query query = NumericRangeQuery.newLongRange("fileSize", 1L, 7L, true, true);


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
    @Test
    public void testBooleanQuery() throws Exception{

        //创建分词器(创建索引和所用时的分词器必须一致)
        Analyzer analyzer = new IKAnalyzer();

        //布尔查询可以根据多个条件组合查询
        //文件名称,包含jsp的并且文件大小大于1小于10的
        BooleanQuery query = new BooleanQuery();

        //根据数字范围查询进行查询
        //查询文件大小大于2小于7的文章
        //第一个参数:域名
        //第二个参数:最小值
        //第三个参数:最大值
        //第四个参数:是否包含最小值
        //第五个参数:是否包含最大值
        Query numberQuery = NumericRangeQuery.newLongRange("fileSize", 1L, 10000L, true, true);

        //创建词元:就是词
        Term term = new Term("fileName","spring");

        //使用TermQuery查询
        //根据term对象进行查询
        TermQuery termQuery = new TermQuery(term);

        query.add(termQuery, BooleanClause.Occur.MUST);
        query.add(numberQuery,BooleanClause.Occur.MUST);


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
    @Test
    public void testMathAllQuery() throws Exception{

        //创建分词器(创建索引和所用时的分词器必须一致)
        Analyzer analyzer = new IKAnalyzer();


        //查询所有文档
        MatchAllDocsQuery query = new MatchAllDocsQuery();




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
    @Test
    public void testMultiFieldQueryParser() throws Exception{

        //创建分词器(创建索引和所用时的分词器必须一致)
        Analyzer analyzer = new IKAnalyzer();

        String[] fileds = {"fileName","fileContext"};

        //从文件名称和文件内容中查询,只要含有spring就查询出来
        MultiFieldQueryParser multiQuery = new MultiFieldQueryParser(fileds,analyzer);
        //需要搜索的关键字
        Query  query = multiQuery.parse("spring");


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

    public static void main(String[] args) {


        int x = 2;
        //                 1   3  2   2
        System.out.println(1 + ++x +2 +x );
        int a = 2;      // 1   3    2  2
        System.out.println(1 + a++ +2 +a );


    }
















}
