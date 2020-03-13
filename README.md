# frameTemplate
基于Springboot+MybatisPlus封装的后台,包含一键生成Model和基础接口  

分页接口  
Dao层  
  List<T> selectBySearch(Page<T> page);  
Service层  
  Result selectBySearch(Integer page, Integer limit);  
实现层  
  @Override  
    public ResultBody selectBySearchInteger page, Integer limit) {  
        Page<T> iPage = new Page<>((page != null) ? page : -1 ,(limit != null) ? limit : -1);  
        iPage.setRecords(dao.selectBySearch(iPage,search,isJoin, isSign));  
        return ResultUtils.getDataForLimit(iPage);  
    }
