package com.pinyougou.seach.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbItem;

public interface SeachItemService {
  public Map seachItem(Map condition);
  public void updateSolr(List<TbItem> list);
  public void deleteSolr(Long[] ids);
}
