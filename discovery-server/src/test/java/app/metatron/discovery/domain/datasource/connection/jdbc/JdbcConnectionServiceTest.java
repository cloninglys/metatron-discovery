/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.metatron.discovery.domain.datasource.connection.jdbc;

import app.metatron.discovery.common.datasource.LogicalType;
import app.metatron.discovery.domain.datasource.Field;
import app.metatron.discovery.domain.datasource.connection.ConnectionRequest;
import app.metatron.discovery.domain.datasource.data.CandidateQueryRequest;
import app.metatron.discovery.domain.datasource.ingestion.jdbc.JdbcIngestionInfo;
import app.metatron.discovery.domain.datasource.ingestion.jdbc.LinkIngestionInfo;
import app.metatron.discovery.domain.workbook.configurations.datasource.DataSource;
import app.metatron.discovery.domain.workbook.configurations.datasource.DefaultDataSource;
import app.metatron.discovery.domain.workbook.configurations.field.DimensionField;
import app.metatron.discovery.domain.workbook.configurations.field.TimestampField;
import app.metatron.discovery.domain.workbook.configurations.filter.Filter;
import app.metatron.discovery.domain.workbook.configurations.filter.InclusionFilter;
import app.metatron.discovery.domain.workbook.configurations.filter.IntervalFilter;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static app.metatron.discovery.domain.datasource.ingestion.jdbc.JdbcIngestionInfo.DataType.TABLE;

/**
 * Created by kyungtaak on 2016. 6. 16..
 */
public class JdbcConnectionServiceTest {

  private JdbcConnectionService jdbcConnectionService = new JdbcConnectionService();

  @Test
  public void checkConnection() {
    MySQLConnection connection = new MySQLConnection();
    connection.setHostname("localhost");
    connection.setDatabase("polaris_datasources");
    connection.setUsername("polaris");
    connection.setPassword("polaris");
    connection.setPort(3306);

    System.out.println(new JdbcConnectionService().checkConnection(connection));

  }

  @Test
  public void checkIrisConnection() {
    MySQLConnection connection = new MySQLConnection();
    connection.setHostname("90.90.200.67");
    connection.setDatabase("??");
    connection.setUsername("??");
    connection.setPassword("??");
    connection.setPort(5050);

    System.out.println(new JdbcConnectionService().checkConnection(connection));

  }

  @Test
  public void selectQueryByTableCase() {
    MySQLConnection connection = new MySQLConnection();
    connection.setHostname("localhost");
    connection.setDatabase("sample");
    connection.setUsername("polaris");
    connection.setPassword("polaris");
    connection.setPort(3306);

    System.out.println(new JdbcConnectionService().selectQueryForIngestion(connection, null,
            TABLE, "sales", 10, true));
  }

  @Test
  public void selectQueryToCsv() {
    MySQLConnection connection = new MySQLConnection();
    connection.setHostname("localhost");
    connection.setUsername("polaris");
    connection.setPassword("polaris");
    connection.setPort(3306);
    connection.setImplementor("MYSQL");

    JdbcIngestionInfo ingestionInfo = new LinkIngestionInfo();
    ingestionInfo.setConnection(connection);
    ingestionInfo.setDataType(TABLE);
    ingestionInfo.setQuery("sales");
    String dataSourceName = "temp_csv001";

    List<Field> fieldList = Lists.newArrayList();

    Field field = new Field();
    field.setName("City");
    field.setAlias("city");
    fieldList.add(field);

    field = new Field();
    field.setName("Category");
    field.setAlias("category");
    fieldList.add(field);

    System.out.println(new JdbcConnectionService().selectQueryToCsv(connection, ingestionInfo, dataSourceName, fieldList, 120));
  }

  @Test
  public void selectQueryFilterToCsv() {
    MySQLConnection connection = new MySQLConnection();
    connection.setHostname("localhost");
    connection.setUsername("polaris");
    connection.setPassword("polaris");
    connection.setPort(3306);
    connection.setDatabase("sample");
    connection.setImplementor("MYSQL");

    JdbcIngestionInfo ingestionInfo = new LinkIngestionInfo();
    ingestionInfo.setConnection(connection);
    ingestionInfo.setDataType(TABLE);
    ingestionInfo.setQuery("sales");
    String dataSourceName = "temp_sample_csv001";

    List<Field> fieldList = Lists.newArrayList();

    Field field = new Field();
    field.setName("City");
    field.setAlias("city");
    fieldList.add(field);

    field = new Field();
    field.setName("Category");
    field.setAlias("category");
    fieldList.add(field);

    field = new Field();
    field.setName("OrderDate");
    field.setAlias("orderDate");
    fieldList.add(field);

    List<Filter> filterList = new ArrayList<>();

    List<String> intervals = Lists.newArrayList("2011-11-04T09:00:00/CURRENT_DATETIME");

    IntervalFilter intervalFilter = new IntervalFilter("OrderDate", intervals);
    filterList.add(intervalFilter);

    List<String> valueList = Lists.newArrayList("Houston", "San Diego");
    InclusionFilter inclusionFilter = new InclusionFilter("City", valueList);
    filterList.add(inclusionFilter);

    System.out.println(new JdbcConnectionService().selectQueryToCsv(connection, ingestionInfo, dataSourceName,
            fieldList, filterList, 120));
  }

  @Test
  public void selectCandidateQueryMysql() {
    MySQLConnection connection = new MySQLConnection();
    connection.setHostname("localhost");
    connection.setDatabase("sample");
    connection.setUsername("polaris");
    connection.setPassword("polaris");
    connection.setPort(3306);
    connection.setImplementor("MYSQL");

    LinkIngestionInfo ingestionInfo = new LinkIngestionInfo();
    ingestionInfo.setDataType(TABLE);
    ingestionInfo.setQuery("sales");

    Field metaDimensionField = new Field();
    metaDimensionField.setName("Category");
    metaDimensionField.setAlias("Category");
    metaDimensionField.setLogicalType(LogicalType.STRING);

    Field metaTimestampField = new Field();
    metaTimestampField.setName("OrderDate");
    metaTimestampField.setAlias("OrderDate");
    metaTimestampField.setLogicalType(LogicalType.TIMESTAMP);

    app.metatron.discovery.domain.workbook.configurations.field.Field dimensionField = new DimensionField("Category", "Category");
    app.metatron.discovery.domain.workbook.configurations.field.Field timestampField = new TimestampField("OrderDate", "OrderDate");

    app.metatron.discovery.domain.datasource.DataSource metaDataSource = new app.metatron.discovery.domain.datasource.DataSource();
    metaDataSource.setConnection(connection);
    metaDataSource.setFields(Lists.newArrayList(metaDimensionField, metaTimestampField));
    metaDataSource.setIngestionInfo(ingestionInfo);
    metaDataSource.setSrcType(app.metatron.discovery.domain.datasource.DataSource.SourceType.JDBC);

    DataSource dataSource = new DefaultDataSource();
    //dataSource.setConnType(app.metatron.discovery.domain.datasource.DataSource.ConnectionType.LINK);
    dataSource.setName("sales");
    dataSource.setMetaDataSource(metaDataSource);

    CandidateQueryRequest queryRequest = new CandidateQueryRequest();
//    queryRequest.setTargetField(dimensionField);
    queryRequest.setTargetField(timestampField);
    queryRequest.setDataSource(dataSource);

    System.out.println(new JdbcConnectionService().selectCandidateQuery(queryRequest));
  }

  @Test
  public void selectCandidateTableTimestampHive() {
    HiveConnection connection = new HiveConnection();
    connection.setHostname("localhost");
    connection.setDatabase("default");
    connection.setUsername("sohncw");
    connection.setPassword("hive");
    connection.setPort(10000);
    connection.setImplementor("HIVE");

    LinkIngestionInfo ingestionInfo = new LinkIngestionInfo();
    ingestionInfo.setDataType(TABLE);
    ingestionInfo.setQuery("sales");

    Field metaDimensionField = new Field();
    metaDimensionField.setName("sales.Category");
    metaDimensionField.setAlias("sales.Category");
    metaDimensionField.setLogicalType(LogicalType.STRING);

    Field metaTimestampField = new Field();
    metaTimestampField.setName("sales.OrderDate");
    metaTimestampField.setAlias("sales.OrderDate");
    metaTimestampField.setLogicalType(LogicalType.TIMESTAMP);

    app.metatron.discovery.domain.workbook.configurations.field.Field dimensionField = new DimensionField("sales.Category", "sales.Category");
    app.metatron.discovery.domain.workbook.configurations.field.Field timestampField = new TimestampField("sales.OrderDate", "sales.OrderDate");

    app.metatron.discovery.domain.datasource.DataSource metaDataSource = new app.metatron.discovery.domain.datasource.DataSource();
    metaDataSource.setConnection(connection);
    metaDataSource.setFields(Lists.newArrayList(metaDimensionField, metaTimestampField));
    metaDataSource.setIngestionInfo(ingestionInfo);
    metaDataSource.setSrcType(app.metatron.discovery.domain.datasource.DataSource.SourceType.JDBC);

    DataSource dataSource = new DefaultDataSource();
    //dataSource.setConnType(app.metatron.discovery.domain.datasource.DataSource.ConnectionType.LINK);
    dataSource.setName("sales");
    dataSource.setMetaDataSource(metaDataSource);

    CandidateQueryRequest queryRequest = new CandidateQueryRequest();
//    queryRequest.setTargetField(dimensionField);
    queryRequest.setTargetField(timestampField);
    queryRequest.setDataSource(dataSource);

    System.out.println(new JdbcConnectionService().selectCandidateQuery(queryRequest));
  }

  @Test
  public void selectCandidateTableDimensionHive() {
    HiveConnection connection = new HiveConnection();
    connection.setHostname("localhost");
    connection.setDatabase("default");
    connection.setUsername("sohncw");
    connection.setPassword("hive");
    connection.setPort(10000);
    connection.setImplementor("HIVE");

    LinkIngestionInfo ingestionInfo = new LinkIngestionInfo();
    ingestionInfo.setDataType(TABLE);
    ingestionInfo.setQuery("sales");

    Field metaDimensionField = new Field();
    metaDimensionField.setName("sales.Category");
    metaDimensionField.setAlias("sales.Category");
    metaDimensionField.setLogicalType(LogicalType.STRING);

    Field metaTimestampField = new Field();
    metaTimestampField.setName("sales.OrderDate");
    metaTimestampField.setAlias("sales.OrderDate");
    metaTimestampField.setLogicalType(LogicalType.TIMESTAMP);

    app.metatron.discovery.domain.workbook.configurations.field.Field dimensionField = new DimensionField("sales.Category", "sales.Category");
    app.metatron.discovery.domain.workbook.configurations.field.Field timestampField = new TimestampField("sales.OrderDate", "sales.OrderDate");

    app.metatron.discovery.domain.datasource.DataSource metaDataSource = new app.metatron.discovery.domain.datasource.DataSource();
    metaDataSource.setConnection(connection);
    metaDataSource.setFields(Lists.newArrayList(metaDimensionField, metaTimestampField));
    metaDataSource.setIngestionInfo(ingestionInfo);
    metaDataSource.setSrcType(app.metatron.discovery.domain.datasource.DataSource.SourceType.JDBC);

    DataSource dataSource = new DefaultDataSource();
    //dataSource.setConnType(app.metatron.discovery.domain.datasource.DataSource.ConnectionType.LINK);
    dataSource.setName("sales");
    dataSource.setMetaDataSource(metaDataSource);

    CandidateQueryRequest queryRequest = new CandidateQueryRequest();
    queryRequest.setTargetField(dimensionField);
//    queryRequest.setTargetField(timestampField);
    queryRequest.setDataSource(dataSource);

    System.out.println(new JdbcConnectionService().selectCandidateQuery(queryRequest));
  }

  @Test
  public void selectCandidateQueryTimestampHive() {
    HiveConnection connection = new HiveConnection();
    connection.setHostname("localhost");
    connection.setDatabase("default");
    connection.setUsername("sohncw");
    connection.setPassword("hive");
    connection.setPort(10000);
    connection.setImplementor("HIVE");

    LinkIngestionInfo ingestionInfo = new LinkIngestionInfo();
    ingestionInfo.setDataType(JdbcIngestionInfo.DataType.QUERY);
    ingestionInfo.setQuery("SELECT * FROM default.sales_part2 where city = 'Alexandria'");

    Field metaDimensionField = new Field();
    metaDimensionField.setName("sales_part2.ymd");
    metaDimensionField.setAlias("sales_part2.ymd");
    metaDimensionField.setLogicalType(LogicalType.STRING);

    Field metaTimestampField = new Field();
    metaTimestampField.setName("sales_part2.OrderDate");
    metaTimestampField.setAlias("sales_part2.OrderDate");
    metaTimestampField.setLogicalType(LogicalType.TIMESTAMP);

    app.metatron.discovery.domain.workbook.configurations.field.Field dimensionField = new DimensionField("sales_part2.ymd", "sales_part2.ymd");
    app.metatron.discovery.domain.workbook.configurations.field.Field timestampField = new TimestampField("sales_part2.OrderDate", "sales_part2.OrderDate");

    app.metatron.discovery.domain.datasource.DataSource metaDataSource = new app.metatron.discovery.domain.datasource.DataSource();
    metaDataSource.setConnection(connection);
    metaDataSource.setFields(Lists.newArrayList(metaDimensionField, metaTimestampField));
    metaDataSource.setIngestionInfo(ingestionInfo);
    metaDataSource.setSrcType(app.metatron.discovery.domain.datasource.DataSource.SourceType.JDBC);

    DataSource dataSource = new DefaultDataSource();
    //dataSource.setConnType(app.metatron.discovery.domain.datasource.DataSource.ConnectionType.LINK);
    dataSource.setName("sales");
    dataSource.setMetaDataSource(metaDataSource);

    CandidateQueryRequest queryRequest = new CandidateQueryRequest();
//    queryRequest.setTargetField(dimensionField);
    queryRequest.setTargetField(timestampField);
    queryRequest.setDataSource(dataSource);

    System.out.println(new JdbcConnectionService().selectCandidateQuery(queryRequest));
  }

  @Test
  public void selectCandidateQueryDimensionHive() {
    HiveConnection connection = new HiveConnection();
    connection.setHostname("localhost");
    connection.setDatabase("default");
    connection.setUsername("sohncw");
    connection.setPassword("hive");
    connection.setPort(10000);
    connection.setImplementor("HIVE");

    LinkIngestionInfo ingestionInfo = new LinkIngestionInfo();
    ingestionInfo.setDataType(JdbcIngestionInfo.DataType.QUERY);
    ingestionInfo.setQuery("SELECT * FROM default.sales_part2 where city = 'Alexandria'");

    Field metaDimensionField = new Field();
    metaDimensionField.setName("sales_part2.ymd");
    metaDimensionField.setAlias("sales_part2.ymd");
    metaDimensionField.setLogicalType(LogicalType.STRING);

    Field metaTimestampField = new Field();
    metaTimestampField.setName("OrderDate");
    metaTimestampField.setAlias("OrderDate");
    metaTimestampField.setLogicalType(LogicalType.TIMESTAMP);

    app.metatron.discovery.domain.workbook.configurations.field.Field dimensionField = new DimensionField("sales_part2.ymd", "sales_part2.ymd");
    app.metatron.discovery.domain.workbook.configurations.field.Field timestampField = new TimestampField("OrderDate", "OrderDate");

    app.metatron.discovery.domain.datasource.DataSource metaDataSource = new app.metatron.discovery.domain.datasource.DataSource();
    metaDataSource.setConnection(connection);
    metaDataSource.setFields(Lists.newArrayList(metaDimensionField, metaTimestampField));
    metaDataSource.setIngestionInfo(ingestionInfo);
    metaDataSource.setSrcType(app.metatron.discovery.domain.datasource.DataSource.SourceType.JDBC);

    DataSource dataSource = new DefaultDataSource();
    //dataSource.setConnType(app.metatron.discovery.domain.datasource.DataSource.ConnectionType.LINK);
    dataSource.setName("sales");
    dataSource.setMetaDataSource(metaDataSource);

    CandidateQueryRequest queryRequest = new CandidateQueryRequest();
    queryRequest.setTargetField(dimensionField);
//    queryRequest.setTargetField(timestampField);
    queryRequest.setDataSource(dataSource);

    System.out.println(new JdbcConnectionService().selectCandidateQuery(queryRequest));
  }

  @Test
  public void getPartitionListTest(){
    String dbName = "default";
    String tableName = "part1";

    Map<String, String> part = new LinkedHashMap<>();
    part.put("ymd", "201101[15-17]");

    List partList = new ArrayList();
    partList.add(part);

    HiveConnection hiveConnection = new HiveConnection();
    hiveConnection.setMetastoreHost("localhost");
    hiveConnection.setMetastorePort("3306");
    hiveConnection.setMetastoreSchema("metastore2");
    hiveConnection.setMetastoreUserName("hiveuser");
    hiveConnection.setMetastorePassword("password");

    ConnectionRequest checkRequest = new ConnectionRequest();
    checkRequest.setDatabase(dbName);
    checkRequest.setType(TABLE);
    checkRequest.setQuery(tableName);
    checkRequest.setPartitions(partList);

    List<Map<String, Object>> partitionInfo = (new JdbcConnectionService()).validatePartition(hiveConnection, checkRequest);
    for(Map<String, Object> partMap : partitionInfo){
      System.out.println(partMap.get("PART_NAME") + ", " + partMap.get("NUM_ROWS"));
    }
  }

  @Test
  public void validatePartitionTest(){
    String dbName = "default";
    String tableName = "part1";

    Map<String, String> part = new LinkedHashMap<>();
    part.put("ymd", "201101[15-17]");

    List partList = new ArrayList();
    partList.add(part);

    HiveConnection hiveConnection = new HiveConnection();
    hiveConnection.setMetastoreHost("localhost");
    hiveConnection.setMetastorePort("3306");
    hiveConnection.setMetastoreSchema("metastore2");
    hiveConnection.setMetastoreUserName("hiveuser");
    hiveConnection.setMetastorePassword("password");

    ConnectionRequest checkRequest = new ConnectionRequest();
    checkRequest.setDatabase(dbName);
    checkRequest.setType(TABLE);
    checkRequest.setQuery(tableName);
    checkRequest.setPartitions(partList);

    List<Map<String, Object>> partitionInfo = (new JdbcConnectionService()).validatePartition(hiveConnection, checkRequest);
    for(Map<String, Object> partMap : partitionInfo){
      System.out.println(partMap.get("PART_NAME") + ", " + partMap.get("NUM_ROWS"));
    }
  }
}
