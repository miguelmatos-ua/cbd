# CBD

## Database Models: Beyond RDBMS

### RDBMS have fundamental issues

In dealing with (horizontal) scale
*   Design to work on single, large machines
*   Difficult to distribute effectively

More subtle: An impendance Mismatch
*   We create logical structures in memory
*   The RBDMS data model often disjoint from intended use
*   Uncomfortable to program with

### Types of NoSql DB
* Key-value stores
* Document stores
* Column stores
* Graph stores

### Key-Value

*Basically Redis*

#### Data model
The most simple NoSql database type
Works as a simple hash table

#### Key-value pairs
Key - usually a string
Value - can be anything

#### Query patterns
Create, update or remove value for a given key
Get value for a given key

#### Characteristics
Great performance, easily scaled,...
Not for complex queries nor complex data

#### Use
Session data, user profiles, user preferences, shopping carts

#### Don't use
Relationships among entities
Queries requiring access to the content of the value part
Set operations involving multiple key-value pairs

### Document Databases
*Basically Json (mongodb)*

#### Use
Event logging, content management systems, blogs, web analitics, e-commerce applications,...

#### Don't use
Set operations involving multiple documents
Design of documents structure is constantly changing

### Column Databases

*Cassandra*

#### Use
Event logging, content management systems, blogs,...

#### Don't use
ACID transactions are required
Complex queries (SUM, AVG,...), joins,...
Early prototypes

### Graph Databases

#### Use
Social networks, routing, dispatch, location-based services,...

#### Don't use
Too large graphs to be stored
Extensive batch operations are required

## Storage and retrieval

### Hash indexes
Key-value stores are like a *dictionary* which is usually implemented as a hash map

A simple indexing strategy: keep an in-memory hash map where every key is mapped to a byte offset in data file

This is essentially what some key-value databases do

### Managing disk space
We avoid running out of space by:
- Creating segments
- Each segment contains all the values written to the database during some period of time
- Performing compaction

The merging and compaction can be done in a background thread
- We can continue to serve read and write requests as normal, using the old segment files

### SSTable

#### Advantages
Merging segments is simple and efficient, even if the files are bigger than the available memory
No need to keep an index of all the keys in memory

### B-trees
B-trees break the database down into fixed-size blocks or pages, traditionally 4 kB in size, and read or write one page at a time.

### Update-in-place vs. append-only log
The basic underlying write operation of a B-tree is to overwrite a page on disk with new data

Some operations require several different pages to be overwritten

If there is a crash after writing only some of the pages, we end up with a corrupted index

To deal with crashed, it is normal to include a write-ahead log

An additional complication of updating pages in-place is that concurrency control is required

This is typically done by protecting the tree's data structures with latches

In this case, log-structured approaches are simpler

## Transaction Processing and Transaction Analytics

### OLTP vs OLAP

**Property** | **OLTP** | **OLAP**
-------------|----------|---------
Main read pattern|Small number of records per query, fetched by key|Aggregate over large number of records
Main write patterns|Random-access, low latency writes from user input|Bulk import (ETL) or event stream
Primarily used by|End user/customer, via web application|Internal analyst, for decision support
What data represents|Latest state of data (current point in time)| History of events that happened over time
Dataset size|Gigabytes to terabytes|Terabytes to petabytes

RDBMS and SQL works well for OLTP-type queries as well as OLAP-type queries.
However, OLAP normally use separate database than OLTP - **data warehouse**

### Data warehousing
- OLTP systems are expected to be highly available and to process transactions with low latency
- A data warehouse is a separate database that analysts can query without affecting OLTP operations
- It typically contains a read-only copy of the data in all the various OLTP systems in the company
- Data is extracted from OLTP databases, transformed, cleaned up, and then loaded into the data warehouse

### Column-oriented Storage
The idea:
- don't store all the values from one row together, but store all the values from each column together instead
- If each column is stored in a separate file, a query only needs to read and parse those columns.

The column-oriented Storage layout relies on each column containing the rows in the same order

To reassemble an entire row *n*, we need to take all the *n* entry from each of the individual column files

### Column compression
Column-oriented Storage often lends itself very well to compression

The sequences of values for each column are often repetitive

Depending on the data in the column, different compression techniques can be used

One technique that is particularly effective in data warehouses is a bitmap encoding

### Column sorting
Normally, columns are stored by the insert order, however, we can choose another order, using one column values.

A second column can determine the sort order of any rows which have the same value in the first sorting column.

Another advantage is that it can help with compression of columns

## Data Formats

- **CSV** - Comma-separated values
- **XML** - Extensible Markup Language
- **JSON** - JavaScript Object Notation
- **BSON** - Binary JSON
- **RDF** - Resource Description Framework
- **Protocol Buffers**

## Key-Value Databases

### Advantages
- Highly fault tolerant - always available

- Schema-less offers easier upgrade path for changing data requirements

- Efficient at retrieving information about a particular object with a minimum of disc operations

- Very simple data model. Very fast to set up and deploy

- Great at scaling horizontally across hundreds or thousands of servers

- No requirements for SQL queries, indexes, triggers, stored procedures, temporary tables, forms, views, or the other technical overheads of RDBMS

- Very high data ingest rates

- Powerful offline reporting with very large data sets.

- Some vendors are offering advanced forms of KVs that approach the capabilities of document stores or column oriented stores

### Disadvantages

- Not suitable for complex applications

- Not efficient at updating records where only a portion of a bucket is to be updated

- Not efficient at retrieving limited data from specific records

- As the volume of data increases maintaining unique values as keys becomes more difficult

- Generally needs to read all the records in a bucket or you may need to construct secondary indexes

## Document Databases

Simpler application code, schema flexibility and better performance due to locality

The **locality advantage** only applies if you need large parts of the document at the same time

### Don't use
Set operations involving multiple documents
Design of document structure is constantly changing

If the application does use **many-to-many relationships**, the document model becomes less appealing

## Column Databases

### Columnar databases

#### Good for:
- Queries that involve only a few columns
- Aggregation queries against vast amounts of data
- Column-wise compression

#### Not good for:
- Incremental data loading
- OLTP usage
- Queries against only a few rows

### (Dis)Advantages

**Advantages**|**Disadvantages**
-|-
Some queries could become really fast|Aggregation is great, but some applications need to show data for each individual record
Better data compression|Writing new data could take more time

### Data Model

#### Column family
- table is a collection of similar rows

#### Row
- row is a collection of columns
- associated with a unique row Key

#### Column
- column consists of a column name and column value
- scalar values, but also sets, lists and maps

### Usage

#### Use
- event logging, content management systems, blogs,...
- batch Processing via map reduce

#### Don't use
- ACID transactions are required
- Complex queries: joining, ...
- Early prototypes
  - i.e. when database design may change
