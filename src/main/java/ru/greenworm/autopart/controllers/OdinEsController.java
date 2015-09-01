package ru.greenworm.autopart.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.greenworm.autopart.services.OdinEsService;

@Controller
@RequestMapping("/odines")
public class OdinEsController {

	@Autowired
	private OdinEsService odinEsService;

	private static Logger logger = LoggerFactory.getLogger(OdinEsController.class);

	private static final int FILE_MAX_SIZE = 1024 * 1024;
	private static final String DIRECTORY = "autopart";

	private String getCheckAuthResponse(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		builder.append("success");
		builder.append("\n");
		builder.append("JSESSIONID");
		builder.append("\n");
		builder.append(request.getSession().getId());
		String response = builder.toString();
		logger.info(response);
		return response;
	}

	private String getInitResponse(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		builder.append("zip=no");
		builder.append("\n");
		builder.append("file_limit=");
		builder.append(FILE_MAX_SIZE);
		String response = builder.toString();
		logger.info(response);
		return response;
	}

	@RequestMapping(params = { "mode=checkauth" })
	public @ResponseBody String checkAuth(HttpServletRequest request) {
		return getCheckAuthResponse(request);
	}

	@RequestMapping(params = { "mode=init" })
	public @ResponseBody String init(HttpServletRequest request) throws IOException {
		Path directoryPath = Paths.get(System.getProperty("java.io.tmpdir"), DIRECTORY);
		FileUtils.deleteDirectory(directoryPath.toFile());
		// Files.deleteIfExists(directoryPath);
		Files.createDirectories(directoryPath, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-x---")));
		return getInitResponse(request);
	}

	@RequestMapping(params = { "mode=file" })
	public @ResponseBody String file(@RequestParam("filename") String filename, @RequestBody byte[] body) throws IOException {
		try {
			Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), DIRECTORY, filename);
			Files.write(filePath, body, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			logger.info(filename);
		} catch (Exception exception) {
			logger.error(exception.getMessage(), exception);
			throw exception;
		}
		return "success";
	}

	@RequestMapping(params = { "mode=import" })
	public @ResponseBody String importt(@RequestParam("filename") String filename) {
		logger.info(filename);
		try {
			odinEsService.processImport(filename);
			return "success";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return getFailureResponse(e);
		}
	}

	private String getFailureResponse(Exception e) {
		StringBuilder builder = new StringBuilder();
		builder.append("failure");
		builder.append("\n");
		builder.append(e.getMessage());
		return builder.toString();
	}

}
