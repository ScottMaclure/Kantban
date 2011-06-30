package extensions;

import org.jsoup.Jsoup;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import play.Logger;
import play.templates.BaseTemplate.RawData;
import play.templates.JavaExtensions;

/**
 * Extensions to manage/manipulate HTML properties in Play models
 * 
 * @author mgjv
 * 
 */
public class HtmlExtensions extends JavaExtensions {

	public static String stripHtml(String html) {
		return Jsoup.parse(html).text();
	}
	
	public static RawData parseMarkdown(String markdown) {
		PegDownProcessor proc = new PegDownProcessor(Extensions.ALL);
		return new RawData(proc.markdownToHtml(markdown));
	}
}
