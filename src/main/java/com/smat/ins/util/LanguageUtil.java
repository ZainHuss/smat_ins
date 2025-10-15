package com.smat.ins.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.smat.ins.util.model.Language;

public class LanguageUtil {
	private static List<Language> languages = new ArrayList<>();
	 private static Map<Locale, List<Language>> languagesMap = new HashMap<>();

	 /**
	  * @return list of all @{link Language} objects
	  */
	 public static List<Language> getAllLanguages() {
	  if (!languages.isEmpty())
	   return languages;

	  /* Get language codes */
	  String[] languageCodes = Locale.getISOLanguages();

	  for (String languageCode : languageCodes) {
	   Locale obj = new Locale(languageCode);
	   Language language = new Language(languageCode,
	     obj.getDisplayLanguage());
	   languages.add(language);
	  }

	  return languages;
	 }

	 /**
	  * @param locale
	  *            User specific locale
	  * @return list of all @{link Language} objects in user specific language
	  */
	 public static List<Language> getAllLanguages(Locale locale) {
	  Objects.nonNull(locale);
	  if (languagesMap.containsKey(locale))
	   return languagesMap.get(locale);

	  List<Language> temp = new ArrayList<>();

	  /* Get language codes */
	  String[] languageCodes = Locale.getISOLanguages();

	  for (String languageCode : languageCodes) {
	   Locale obj = new Locale(languageCode);
	   Language language = new Language(languageCode,
	     obj.getDisplayLanguage(locale));
	   temp.add(language);
	  }

	  languagesMap.put(locale, temp);
	  return languagesMap.get(locale);
	 }
}
