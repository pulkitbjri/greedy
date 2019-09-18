# GreedyImageLoader

To get images use the function below

GreedyImageLoader.with(context)
.setCachingType(GreedyImageLoader.DISK) 
.load(imageView,imageUrl);

Functions:
load(imageview,imageurl)
  pass imageview annd imageurl
setChachingType(int savestrategy)
  savestrategy can be of two type - 1.GreedyImageLoader.DISK
                                    2.GreedyImageLoader.MEMORY
                                    
