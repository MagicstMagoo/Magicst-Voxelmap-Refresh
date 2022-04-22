 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.atomic.AtomicInteger;
 import org.jetbrains.annotations.NotNull;
 class NamedThreadFactory
   implements ThreadFactory
 {
   private final String name;
   private final AtomicInteger threadCount = new AtomicInteger(1);
   
   public NamedThreadFactory(String name) {
     this.name = name;
   }
   
   public Thread newThread(@NotNull Runnable runnable) {
     return new Thread(runnable, this.name + " " + this.name);
   }
 }

