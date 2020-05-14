package com.lee.juc.completablefuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * 尚硅谷电商项目谷粒商城 P95 completableFuture改造商品详情页
 */
@Service
public class ItemService {

//    @Autowired
//    private GmallWmsClient wmsClient;
//    @Autowired
//    private GmallSmsClient smsClient;
//    @Autowired
//    private GmallPmsClient pmsClient;
//    @Autowired
//    private ThreadPoolExecutor threadPoolExecutor;
//
//    public ItemVo queryItemVo(Long skuId) {
//        ItemVo itemVo = new ItemVo();
//        // 设置skuid
//        itemVo.setSkuId(skuId);
//        // 1、根据id查询sku
//        CompletableFuture<Object> skuCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            Resp<SkuInfoEntity> skuResp = this.pmsClient.querySkuById(skuId);
//            SkuInfoEntity skuInfoEntity = skuResp.getData();
//            if (skuInfoEntity == null) {
//                return itemVo;
//            }
//            itemVo.setSkuTitle(skuInfoEntity.getSkuTitle());
//            itemVo.setSubTitle(skuInfoEntity.getSkuSubtitle());
//            itemVo.setPrice(skuInfoEntity.getPrice());
//            itemVo.setWeight(skuInfoEntity.getWeight());
//            itemVo.setSpuId(skuInfoEntity.getSpuId());
//            // 获取spuid
//            return skuInfoEntity;
//        }, threadPoolExecutor);
//
//        // 2、根据sku中的spuid查询spu
//        CompletableFuture<Void> spuCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
//            Resp<SpuInfoEntity> spuResp = this.pmsClient.querySpuById(((SkuInfoEntity) skuInfoEntity).getSpuId());
//            SpuInfoEntity spuInfoEntity = spuResp.getData();
//            if (spuInfoEntity != null) {
//                itemVo.setSpuName(spuInfoEntity.getSpuName());
//            }
//        }, threadPoolExecutor);
//
//        // 3、根据skuid查询图片列表
//        CompletableFuture<Void> imageCompletableFuture = CompletableFuture.runAsync(() -> {
//            Resp<List<SkuImagesEntity>> skuImagesResp = this.pmsClient.querySkuImagesBySkuId(skuId);
//            List<SkuImagesEntity> skuImagesEntities = skuImagesResp.getData();
//            itemVo.setPics(skuImagesEntities);
//        }, threadPoolExecutor);
//
//        // 4、根据sku中的brandid和categoryid查询品牌和分类
//        CompletableFuture<Void> brandCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
//            Resp<BrandEntity> brandEntityResp = this.pmsClient.queryBrandsById(((SkuInfoEntity) skuInfoEntity).getBrandId());
//            BrandEntity brandEntity = brandEntityResp.getData();
//            itemVo.setBrandEntity(brandEntity);
//        }, threadPoolExecutor);
//
//        CompletableFuture<Void> cateCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
//            Resp<CategoryEntity> categoryEntityResp = this.pmsClient.queryCategoryById(((SkuInfoEntity) skuInfoEntity).getCatalogId());
//            CategoryEntity categoryEntity = categoryEntityResp.getData();
//            itemVo.setCategoryEntity(categoryEntity);
//        }, threadPoolExecutor);
//
//        // 5、根据skuid查询营销信息
//        CompletableFuture<Void> saleCompletableFuture = CompletableFuture.runAsync(() -> {
//            Resp<List<SaleVo>> salesResp = this.smsClient.querySalesBySkuId(skuId);
//            List<SaleVo> saleVoList = salesResp.getData();
//            itemVo.setSales(saleVoList);
//        }, threadPoolExecutor);
//
//        // 6、根据skuid查询库存
//        CompletableFuture<Void> storeCompletableFuture = CompletableFuture.runAsync(() -> {
//            Resp<List<WareSkuEntity>> wareResp = this.wmsClient.queryWareSkuBySkuId(skuId);
//            List<WareSkuEntity> wareSkuEntities = wareResp.getData();
//            itemVo.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
//        }, threadPoolExecutor);
//
//        // 7、根据spuid查询所有skuid，然后再去查询所有的销售属性
//        CompletableFuture<Void> saleAttrCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
//            Resp<List<SkuSaleAttrValueEntity>> saleAttrValueResp = this.pmsClient.querySkuSalesAttrValuesBySpuId(((SkuInfoEntity) skuInfoEntity).getSpuId());
//            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = saleAttrValueResp.getData();
//            itemVo.setSalAttrs(skuSaleAttrValueEntities);
//        }, threadPoolExecutor);
//
//        // 8、根据spuid查询商品描述（海报）
//        CompletableFuture<Void> descCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
//            Resp<SpuInfoDescEntity> spuInfoDescEntityResp = this.pmsClient.querySpuDescBySpuId(((SkuInfoEntity) skuInfoEntity).getSpuId());
//            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescEntityResp.getData();
//            if (spuInfoDescEntity != null) {
//                String decript = spuInfoDescEntity.getDecript();
//                String[] split = StringUtils.split(decript, ",");
//                itemVo.setImages(Arrays.asList(split));
//            }
//        }, threadPoolExecutor);
//
//        // 9、根据cateid和spuid查询组及组下的规格参数（带值的）
//        CompletableFuture<Void> groupCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
//            Resp<List<ItemGroupVo>> itemGroupResp = this.pmsClient.queryItemGroupVoByCidAndSpuId(((SkuInfoEntity) skuInfoEntity).getCatalogId(), ((SkuInfoEntity) skuInfoEntity).getSpuId());
//            List<ItemGroupVo> itemGroupVos = itemGroupResp.getData();
//            itemVo.setGroups(itemGroupVos);
//        }, threadPoolExecutor);
//
//        CompletableFuture.allOf(spuCompletableFuture,
//                imageCompletableFuture,
//                brandCompletableFuture,
//                cateCompletableFuture,
//                saleCompletableFuture,
//                storeCompletableFuture,
//                saleAttrCompletableFuture,
//                descCompletableFuture,
//                groupCompletableFuture).join();
//        return itemVo;
//    }

    public static void main(String[] args) {
        CompletableFuture.allOf(
                CompletableFuture.completedFuture("hello, completedFuture1"),
                CompletableFuture.completedFuture("hello, completedFuture2"),
                CompletableFuture.completedFuture("hello, completedFuture3"),
                CompletableFuture.completedFuture("hello, completedFuture4")
        ).join();


        CompletableFuture.supplyAsync(
                () -> {
                    System.out.println("runAsync.......");
                    //                    int i = 1 / 0;
                    return "hello, supplyAsync";
                })
                .thenApply(
                        t -> {
                            System.out.println(t);
                            return "hello, apply1";
                        })
                .thenApply(
                        t -> {
                            System.out.println(t);
                            return "hello, apply2";
                        })
                .thenAccept(
                        t -> {
                            System.out.println(t);
                        })
                .whenComplete(
                        (t, u) -> {
                            System.out.println(t);
                            System.out.println(u);
                        })
                //                .exceptionally(
                //                        t -> {
                //                            System.out.println(t);
                //                            return "hello exception";
                //                        })
                .handleAsync(
                        (t, u) -> {
                            System.out.println(t);
                            System.out.println(u);
                            return "hello, handle";
                        })
                .thenCombine(
                        CompletableFuture.completedFuture("hello completedFuture"),
                        (t, u) -> {
                            System.out.println(t);
                            System.out.println(u);
                            return "hello combine";
                        });

        // 1、继承Thread类
        //        new MyThread().start();
        // 2、实现Runnable接口
        //        new Thread(new MyRunnable()).start();
        // 3、callable接口，有返回值，可以处理异常
        //        FutureTask<String> futureTask = new FutureTask<>(new MyCallable());
        //        new Thread(futureTask).start();
        //        try {
        //            System.out.println(futureTask.get());
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        } catch (ExecutionException e) {
        //            e.printStackTrace();
        //        }
        // 4、线程池
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
//                3,
//                5,
//                50,
//                TimeUnit.SECONDS,
//                new ArrayBlockingQueue<>(10),
//                Executors.defaultThreadFactory(),
//                (Runnable r, ThreadPoolExecutor executor) -> {
//                    System.out.println("执行了拒绝策略");
//                });
//        for (int i = 0; i < 50; i++) {
//            threadPoolExecutor.execute(() -> {
//                System.out.println("thread start" + Thread.currentThread().getName());
//                System.out.println("-====================");
//                System.out.println("thread end");
//            });
//        }


    }
}

class MyCallable implements Callable<String> {

    @Override
    public String call() throws Exception {
        System.out.println("thread start" + Thread.currentThread().getName());
        System.out.println("-====================");
        System.out.println("thread end");
        return "hello";
    }
}

class MyRunnable implements Runnable {

    @Override
    public void run() {
        System.out.println("thread start" + Thread.currentThread().getName());
        System.out.println("-====================");
        System.out.println("thread end");
    }
}


class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("thread start");
        System.out.println("-====================");
        System.out.println("thread end");
    }
}
