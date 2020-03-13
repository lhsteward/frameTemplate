package com.lhc.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lhc.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;


/**
 * MybatisPlus  Controller封装类
 * @param <T> Model
 * @param <S> Service
 * @param <KEY> 主键类型
 */
// 方法级校验
@Validated
public class BaseController<T extends Model<T>, S extends IService<T>, KEY extends Serializable> {

    @Autowired
    S service;

    /**
     * 条件查询和分页  page/limit 分页必须都大于0
     * @param t
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/get")
    public Result get(T t, Integer page, Integer limit){
        QueryWrapper<T> query = new QueryWrapper<>();
        Class cls = t.getClass();
        Field[] fields = cls.getDeclaredFields();
        boolean flag = false;
        String isDel = null;
        for (Field field : fields) {
            field.setAccessible(true);
            if(!"isDel".equalsIgnoreCase(field.getName()) && !"is_del".equalsIgnoreCase(field.getName())){
                flag = true;
                isDel = field.getName();
            }
            try {
                if(null != field.get(t) && "".equals(field.get(t))){
                    query.eq(field.getName(), field.get(t));
                }
                if(flag){
                    query.eq(isDel, 0);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(page != null && limit != null && page > 0 && limit > 0){
            IPage<T> iPage = service.page(new Page<>(page, limit),query);
            return  ResultUtils.getDataForLimit((int)iPage.getTotal(),iPage.getRecords());
        }
        List<T> list = service.list(query);
        return ResultUtils.success(list == null ? 0 : list.size(),list);
    }

    /**
     * 查询方法
     * @return
     */
    @GetMapping("/{id}")
    public Result selectById(@PathVariable @NotNull(message = "ID不能为空") KEY id){
        return ResultUtils.success(service.getById(id));
    }

    /**
     * 批量查询 格式 : 'x,x' 例子：'1,2,3,4'
     * @param ids
     * @return
     */
    @GetMapping("/selectByIds")
    public Result selectByIds(@NotBlank(message = "ID字符串不能为空") String ids){
        List<String> list  = Arrays.asList(ids.split(","));
        return ResultUtils.success(service.listByIds(list));
    }

    /**
     * 添加接口 验证
     * @param t
     * @return
     */
    @PostMapping("insert")
    public Result insert(@Valid T t){
        return t.insert() ? ResultUtils.success(): ResultUtils.error();
    }

    /**
     * 批量添加
     * @param list
     * @return
     */
    @PostMapping("batchInsert")
    public Result batchInsert(@RequestBody List<@Valid T> list){
        if(list.size() == 0){
            return ResultUtils.error(Code.MISS_REQUIRED_PARAMETER);
        }
        return service.saveBatch(list,list.size()) ? ResultUtils.success() : ResultUtils.error();
    }

    /**
     * 修改接口  ID必传 无法校验参数  建议重写并校验
     * @param t
     * @return
     */
    @PostMapping("update")
    public Result update(T t){
        return t.updateById() ? ResultUtils.success(): ResultUtils.error();
    }

    /**
     * 根据ID删除
     * @param id
     * @return
     */
    @PostMapping("/{id}")
    public Result deleteById(@PathVariable @NotNull(message = "ID不能为空") KEY id){
        return service.removeById(id) ? ResultUtils.success(): ResultUtils.error();
    }

    /**
     * 批量删除 格式 : 'x,x' 例子：'1,2,3,4'
     * @param ids
     * @return
     */
    @PostMapping("/deleteByIds")
    public Result deleteByIds(@NotBlank(message = "ID字符串不能为空") String ids){
        List<String> list  = Arrays.asList(ids.split(","));
        return ResultUtils.success(service.removeByIds(list));
    }

}
