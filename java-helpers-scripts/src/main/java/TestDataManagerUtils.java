import exceptions.SomeTestException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@UtilityClass
public class TestDataManagerUtils {

	private static final String PATH_TO_TEST_DATA = "module/src/test/resources/testdata";
	private static final String FILE_DOES_NOT_EXIST_ERROR = "File %s does not exist!";


	private static Map<String, String> testDataFileNames = new HashMap<>();

	/**
	 * If you put relative paths in your steps in tests, you can use this method to beautify ones<br>
	 * K - filename, V - relative path to file<br>
	 *
	 * Use it with `getRelativePathToFile(String filePath)`
	 */
	public static void setupTestDataFileNames() {
		Path testDataDirectoryClassLoader = new File(Objects.requireNonNull(
						TestDataManagerUtils.class.getClassLoader().getResource(PATH_TO_TEST_DATA))
				.getFile()).toPath();
		try (final Stream<Path> walk = Files.walk(testDataDirectoryClassLoader)) {
			testDataFileNames = walk.filter(Predicate.not(Files::isDirectory))
					.map(testDataDirectoryClassLoader::relativize)
					.collect(Collectors.toMap(path -> path.getFileName().toString(), Path::toString,
							(file, duplicate) -> {
								throw new SomeTestException(
										"\nFile \"" + file + "\" has the same name as a file \"" + duplicate + "\"");
							}));
		} catch (IOException e) {
			throw new SomeTestException("Error during scanning path " + testDataDirectoryClassLoader, e);
		}
	}

	private static String getRelativePathToFile(String filePath) {
		if (Objects.isNull(testDataFileNames)) {
			String relativePath = testDataFileNames.get(filePath);
			if (Objects.isNull(relativePath)) {
				throw new SomeTestException(String.format(FILE_DOES_NOT_EXIST_ERROR, filePath));
			}
			return relativePath;
		}
		return filePath;
	}

	/**
	 * Method for changing filenames
	 *
	 * @param regexFilter  filter the path where you need to change filenames
	 * @param replaceRegex regex of part that you want to change
	 * @param replaceToStr replace to
	 * @throws IOException exception
	 */
	public static void renameFiles(String regexFilter, String replaceRegex, String replaceToStr)
			throws IOException {
		try (final Stream<Path> walk = Files.walk(Paths.get(PATH_TO_TEST_DATA))) {
			walk.filter(Files::isRegularFile)
					.filter(f -> f.getFileName().toString().matches(regexFilter)).forEach(path -> {
						File newFile = new File(
								path.toFile().getAbsolutePath().replaceFirst(replaceRegex, replaceToStr));
						if (path.toFile().renameTo(newFile)) {
							log.info("\nFile {} renamed to: {}", path.toAbsolutePath(),
									newFile.getAbsolutePath());
						}
					});
		}
	}

	/**
	 * Filter files
	 *
	 * @param regexFilter filter files in the path
	 * @return filtered files
	 */
	public static List<File> filterFilesByRegex(String regexFilter) {
		List<File> fileList = new ArrayList<>();
		try (final Stream<Path> walk = Files.walk(Paths.get(PATH_TO_TEST_DATA))) {
			walk.filter(Files::isRegularFile)
					.filter(f -> f.toAbsolutePath().toString().matches(regexFilter))
					.forEach(path ->
							fileList.add(new File(path.toFile().getAbsolutePath()))
					);
		} catch (IOException e) {
			log.info("Something went wrong during scanning the path" + regexFilter);
		}
		String collect = fileList.stream()
				.map(File::getName)
				.sorted()
				.collect(Collectors.joining("\t\n"));
		log.info("Found files\n" + collect);
		return fileList;
	}

	/**
	 * Delete files by regex
	 *
	 * @param regexFilter filter files in the path
	 * @throws IOException exception
	 */
	public static void deleteFiles(String regexFilter) throws IOException {
		try (final Stream<Path> walk = Files.walk(Paths.get(PATH_TO_TEST_DATA))) {
			walk.filter(f -> f.getFileName().toString().matches(regexFilter)).forEach(path -> {
				final boolean deleted = path.toFile().delete();
				log.info("\nFile {} has been deleted {}", path.toAbsolutePath(), deleted);
			});
		}
	}

	/**
	 * Delete empty folders<br>
	 * It's good to use after deleteFiles()
	 *
	 * @throws IOException exception
	 * @see TestDataManagerUtils#deleteFiles(String)
	 */
	public static void deleteEmptyFolders() throws IOException {
		try (final Stream<Path> walk = Files.walk(Paths.get(PATH_TO_TEST_DATA))) {
			walk.filter(Files::isDirectory).map(path -> new File(path.toString())).forEach(path -> {
				if (path.length() == 0) {
					final boolean deleted = path.delete();
					if (deleted) {
						log.info("\nDirectory {} has been deleted", path);
					}
				}
			});
		}
	}
}
