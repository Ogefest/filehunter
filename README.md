# filehunter 

Simple, fast, open source file search engine. Designed to be local file search engine for places where multiple documents 
are stored on multiple hosts with multiple directories.

## Core features

- Blazing fast search through your files in all indexes
- Parsing documents and index content for full text search
- Multiple backends supported, search files available on local filesystem, WebDAV, S3, FTP, NFS
- Nice and fast React based frontend
- Offline indexes - reindex directory and search for files even on umounted drives
- Open Source, self-hosted solution, easy to start using single file
- API driven architecture - every operation you can perform using REST API

## Get started
0. Java 11 or higher is required to run `FileHunter`
1. Download the latest version from [releases in Github](https://github.com/Ogefest/filehunter/releases)
2. Create separate directory where you put jar file
3. Run `java -jar filehunter-<version>.jar`
4. Open in browser `http://<HOST_IP>:8034`
5. Create new index using GUI with name and directory

## Screenshot

Have a look on web based GUI

![screenshot](imgs/fh_search.png)
![screenshot](imgs/fh_filters.png)
![screenshot](imgs/fh_startup.png)
![screenshot](imgs/fh_indexes.png)



## Configuration variables
Filehunter might be configured using env variables. Lists of variables is available below. 
You can set this value `-Dvar.name=var.value -Dvar2.name=var2.value` for example 
`java -Dfilehunter.storage.directory=/tmp/myindexdirectory -jar filehunter-<version>.jar`


| var | description | default value|
|-----|-------------|---------------|
| filehunter.storage.directory | Directory for index data | `./filehunterstorage` |
| quarkus.http.host | Host used by app | `0.0.0.0` |
| quarkus.http.port | Port used by app | `8034` |


## Support

If you need additional support or dedicated component/integration/feature I'm open to develop
it. Reach me at hello@ogefest.com

## FAQ
- Is Filehunter ready and steady?
  - Nope, right now is in early development stage and probably has some bugs which might make it hard to use
- I found a bug, where I can report it?
  - The easiest way is to open issue using Github
- I've added a new index but when I try to search for something I got empty results, what is wrong?
  - Is this directory was indexed? Filehunter needs a few seconds or minutes or hours (depends on how many files are in the directory) after the index was created to be able to search for something. Check the date in indexes when directory reindex was finished.
- How long may take to reindex whole directory?
  - It depends on how fast is access to your storage. For example, NMVe drive and directory with 120k files with size of 5GB first reindex take about 30 seconds. 800k files with size of 1 TB over NAS storage take 30 minutes for first reindex
- How to change host and/or port?
  - Run with parameters `java -Dquarkus.http.port=8888 -Dquarkus.http.host=127.0.0.1 -jar filehunter-<version>.jar` You can change all parameters from [this list](https://quarkus.io/guides/all-config)
- Files in my directory changed frequently, when new files will be available to search?
  - It depends on index configuration. Check option `File structure interval` in index configuration

