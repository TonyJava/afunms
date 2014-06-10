package com.bpm.process.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bpm.process.dao.ExportExcelDao;
import com.bpm.process.model.SearchModel;

@Service
@Transactional
public class ExportExcelService {
	@Resource
	private ExportExcelDao exportExcelDao;

	public String export(String type, SearchModel model) {
		return exportExcelDao.export(type, model);
	}

}
