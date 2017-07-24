# DownCacheDroid
A library to effectively cache & download web files.

This library utilizes [okhttp3](https://github.com/square/okhttp) and [Dagger 2](https://github.com/google/dagger) as DI framework

**WARNING :
The library is currently is a bit slow during initialization, but should be smoother thereafter.
This seems to be caused by the inclusion of callback in detecting MIME type asynchronously.
I will fix this in the future.**

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
The `cacheDroidModule(new CacheDroidModule(typeSet))` specify the supported set of wrapper data
for the cache.
New type can be injected later by calling `cacheDroidModule.addNewSupportedType` method.

## 1.1 Downloading & Caching
Downloading is performed asynchronously and in parallel.
Caching is created based on LRU (Least-Recently-Used) algorithm, and the access is thread-safe.
All methods in this section are accessed from instance of `DownloadProcDroid` class.

**1.Download And Cache**

```
cacheWebContents(String url)
```

`url` refers to the url pointing to the web crawl content.


**2. Get all URLs from a webpage**

```
getWebResLinks(String url, GenericCallback<List<String>> successCallback)
```

The `sucessCallback` is provided with a list of URL in the `onValue` method
detected in a given webcrawl content through callback.

**3. Retry failed downloads**

```
asyncRedownloadFailedAll(GenericCallback<String> successCallback)
```

**4. Cancel download**
```
cancelDownload(String url)
```

## 1.2 Accessing Cache
The cache is stored in the format of key (URL) & value (Pair of cached in Java object and object representing types of data)
All methods mentioned in this section are available from `CacheDroidModule` class.

**1.Obtaining the cache instance**

```
downCacheApp.getDownloadProcInstance().cacheDroidModule
```

**2.Insert to cache**

```
insertToCache(String key, Object data, BaseDownFileModule downFileType)
```

`downFileType` is an existing/user-created subclass of BaseDownFileModule.


**3. Get data from cache**

```
getConvertedDataFromCache(String key)
```

Returns object with concrete type as specified in the method `convertDownloadedData` in subclass of `BaseDownFileModule`.

**4. Get All key/urls from cache**

```
getAllKeys()
```


**5. Get type of the wrapper of the cache data**
```
getTypeFromCache(String key)
```

Returns the type of wrapper of the data


**6.Add support for new wrapper type**
```
addNewSupportedType(BaseDownFileModule baseDownFileModule)
```

Add new supported wrapper to be cachable.

**7.Get all supported type of wrapper of the cache data**
```
getAllSupportedTypes()
```


**8. Set Data Update Callback**

```
setDataUpdateListener(DataUpdateListener dataUpdateListener)
```
The method is used to keep track of cache element update and removal.
`dataUpdateListener` is composed 2 types of events :
```
    void cacheElemAdded(String url);
    void cacheElemRemoved(String url);
```
Override both of them in your view-related classes with function you want to execute on the events.


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





