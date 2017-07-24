# DownCacheDroid
A library to effectively cache & download web files.
This library utilizes [okhttp3](https://github.com/square/okhttp) and [Dagger 2](https://github.com/google/dagger) as DI framework

# 1. Features & Usage
Firstly, build the project so Dagger 2 can generate the Dagger-prefixed class.
Then, to use the library, at least a module of type `DownCacheApp` is needed to be created :
```
Set<BaseDownFileModule> typeSet = new HashSet<BaseDownFileModule>();
typeSet.add(new ImageDownFileModule());

DownCacheApp downCacheApp = DaggerDownCacheApp
                            .builder()
                            .cacheDroidModule(new CacheDroidModule(typeSet))
                            .build();
```
`DownCacheApp` is the main interface of the library for interaction between user.
A single instance of this interface represents a single cache.
The variable `typeSet` refers to the type you'd like to support.
New type can be injected by calling :
 `cacheDroidModule.addNewSupportedType` method.
You can get a list of supported types by calling :
`cacheDroidModule.getAllSupportedTypes()`

## 1.1 Downloading & Caching
Downloading is performed asynchronously and in parallel.
Caching is created based on LRU (Least-Recently-Used) algorithm, and the access is thread-safe.
All methods in this section are accessed from instance of `DownloadProcDroid` class.

**1.Download And Cache**

`cacheWebContents(String url)`
`url` refers to the url pointing to the web crawl content.

**2.Retry failed downloads**

`asyncRedownloadFailedAll(GenericCallback<String> successCallback)`

## 1.2 Accessing Cache
The cache is stored in the format of key (URL) & value (Pair of cached in Java object and object representing types of data)
All methods mentioned in this section are available from `CacheDroidModule` class.
**1.Obtaining the cache instance**

`downCacheApp.getDownloadProcInstance().cacheDroidModule`

**2.Insert to cache**

`insertToCache(String key, Object data, BaseDownFileModule downFileType)`
`downFileType` is an existing/user-created subclass of BaseDownFileModule.

**3. Get data from cache**
``getConvertedDataFromCache(String key)``
Returns object with concrete type as specified in the method `convertDownloadedData` in subclass of `BaseDownFileModule`.

**Set Data Update Callback**

`setDataUpdateListener(DataUpdateListener dataUpdateListener)`
`dataUpdateListener` is composed 2 types of events, when data is added and when data is removed.


## 1.3 Creating Custom Downloadable Types
Users of library can create their own class to support new downloadable types, by extending their class from `BaseDownFile` class.
Users will be required to specify the MIME, which represents what type of Content-Type the class should represent as.
and also to override 3 methods, all of which are located in `BaseDownFileModule` class as abstract methods.


**1.Convert Downloaded Data**
```
public Object convertDownloadedData(byte[] networkInput)
```
Convert a downloaded data in byte to your desired type.
Please note that `convertDownloadedData` and `getConvertedData` method has to return the same instantiated type of object if
you decide to override them.

**2.Get Data:**
```
    @Override
    public Object getConvertedData(Object data) {
```
The method returns object with the same instantiated type as specified by `convertDownloadedData` method above.
Please note that `convertDownloadedData` and `getConvertedData` method has to return the same instantiated type of object if
you decide to override them.

**3.Get Object Type Of Web Data :**
```
    public void download(Function<BaseDownFileModule, Call> standardDownload) throws IOException
```
Specify how a custom download process for the object representing the class in which the method is residing in
to be performed


## 1.4 Cancel download
You can cancel existing download in progress by calling `downCacheApp.getDownloadProcInstance().cancelDownload(String url)`.




