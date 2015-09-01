package ru.greenworm.autopart.services;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.greenworm.autopart.dao.SettingDao;
import ru.greenworm.autopart.model.Setting;

@Service
public class SettingsService {

	@Autowired
	private SettingDao settingDao;

	public static final Map<String, String> CACHE = new ConcurrentHashMap<String, String>();

	public String getValue(String key) {
		String value = CACHE.get(key);
		if (value == null) {
			Setting setting = settingDao.getSetting(key);
			value = setting != null ? setting.getValue() : null;
		}
		if (value != null) {
			CACHE.put(key, value);
		}
		return value;
	}

	public Setting setValue(String key, String value) {
		Setting setting = settingDao.getSetting(key);
		if (setting == null) {
			setting = new Setting();
			setting.setKey(key);
		}
		setting.setValue(value);
		settingDao.saveOrUpdate(setting);
		CACHE.put(key, value);
		return setting;
	}

	public void createIfNotExists(String key, String value) {
		Setting setting = settingDao.getSetting(key);
		if (setting == null) {
			setting = new Setting();
			setting.setKey(key);
			setting.setValue(value);
			settingDao.save(setting);
		}
	}

	public Integer getAsInteger(String key) {
		try {
			return Integer.parseInt(getValue(key));
		} catch (Exception e) {
			// ignore
			return null;
		}
	}

	public List<Setting> getAll() {
		return settingDao.getAll();
	}

}
