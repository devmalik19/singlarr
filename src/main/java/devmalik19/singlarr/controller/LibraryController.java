package devmalik19.singlarr.controller;

import devmalik19.singlarr.constants.FolderType;
import devmalik19.singlarr.data.dao.Library;
import devmalik19.singlarr.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LibraryController
{
	private final LibraryService libraryService;

	public LibraryController(LibraryService libraryService)
	{
		this.libraryService = libraryService;
	}

	@GetMapping("/library")
	public String library(Model model)
	{
		model.addAttribute("library", libraryService.getByType(FolderType.ARTIST));
		return "library";
	}

	@GetMapping("/library/{id}")
	public String items(@PathVariable("id") Integer id, Model model)
	{
		Library library = libraryService.findById(id);

		model.addAttribute("library", library);
		model.addAttribute("libraries", library.getLibraryList());
		model.addAttribute("items", library.getItemList());

		return "items";
	}

	@PostMapping("/library/metadata/{id}")
	public String refreshMetadata(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes)
	{
		try
		{
			libraryService.refreshMetadata(id);
			redirectAttributes.addFlashAttribute("message", "Metadata refreshed successfully.");
		}
		catch (Exception e)
		{
			redirectAttributes.addFlashAttribute("error", "Metadata refresh failed: " + e.getMessage());
		}
		return "redirect:/library/" + id;
	}

	@PostMapping("/library/metadata/all")
	public String refreshAllMetadata(RedirectAttributes redirectAttributes)
	{
		int count = libraryService.resetAllMetadataFlags();
		redirectAttributes.addFlashAttribute("message", count + " library entries reset. Metadata will be re-fetched on next scan.");
		return "redirect:/library";
	}
}
