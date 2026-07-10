package devmalik19.singlarr.controller;

import devmalik19.singlarr.constants.FolderType;
import devmalik19.singlarr.data.dao.Library;
import devmalik19.singlarr.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LibraryController
{
	@Autowired
	private LibraryService libraryService;

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
}
