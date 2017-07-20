# DownCacheDroid
A library to effectively cache & download web files.


# 1.Requirements

```
1. okhttp 3.x
```

# 2. Features & Usage
## 2.1 Caching
Caching is created using LRU (Least-Recently-Used) algorithm.
To cache, first determine what types of file you'd like to download by calling
`CacheDroid.supportedDownTypes` method. For example , to download all images file given
a textual representation of web data, call :
` CacheDroid.supportedDownTypes.add(new DownImageFile());`.
Custom types are supported by extending from
`DownCacheDroid\app\src\main\java\mrrexz\github\com\downcachedroid\model\downfiles\BaseDownFile`.
After determining the supported data type, provides a URL string containing textual representation
of web data and execute the `DownloadProcDroid.cacheWebContents(String url)` method.


## 2.2 Getting Information From Cache
The cache is stored in the format of key (URL) & value (Pair of cached web data and object representing types of data)
There are 3 main methods provided for user, all of which are accessed from `DownCacheDroid\app\src\main\java\mrrexz\github\com\downcachedroid\model\caching\CacheDroid` class :

**1.Get Uncompressed Data :**
```
public synchronized static Object getConvertedDataFromCache(String key)
```
The method returns object fully & safely castable to type as specified in the
`get` method of the class that extends from `BaseDownFile`.

**2.Get Content of Web Data :**
```
public synchronized static InputStream getDataFromCache(String key)
```
Returns an InputStream which the user can perform casting to the desired data.

**3.Get Object Type Of Web Data :**
```
public synchronized static BaseDownFile getTypeFromCache(String key)
```
Returns the "type" of data.
Users can create his/her own class type that extends from `BaseDownFile`, and call
any functions against them polymorphically.

## 2.3 Creating Custom Downloadable Types
Users of library can create their own class and extend from `BaseDownFile` class.
Users will be required to specify the MIME, which represents what type of Content-Type the class should represent as.
and also to override 2 methods; `get` and `download`.
`get` method allows the users to perform casting to his/her desired type from data stored in
the format of `InputStream` in cache.
`download` method allows the users to put his/her own custom download algorithm for the element of the `Content-Type` that mathces with
the current object's MIME value.


# 3.TODOs:
```
3.1 Implement test units
3.2 Implement Demo
3.3 Utilizes NDK
```


