package com.javacodegeeks.service;

import java.net.URISyntaxException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javacodegeeks.dao.StatDao;

@Service
public class StatServiceImpl implements StatService {

	//@Autowired
	private StatDao statDao;
	
	public int read(int year, int month) throws SQLException, URISyntaxException {
		return statDao.read(year, month);
	}

	public void insert(int year, int month) throws SQLException, URISyntaxException {
		statDao.insert(year, month);
	}

	public void update(int year, int month, int count) throws SQLException, URISyntaxException {
		statDao.update(year, month, count);
	}

}
