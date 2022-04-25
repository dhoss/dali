package in.stonecolddev.dali.image;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/api/image",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ImageController {

    @GetMapping("/{slug}")
    public String find(@PathVariable String slug) {
        return slug;
    }
}