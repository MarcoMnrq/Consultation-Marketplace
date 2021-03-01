package com.marcomnrq.consultation.controller;

import com.marcomnrq.consultation.domain.model.Listing;
import com.marcomnrq.consultation.domain.model.Professional;
import com.marcomnrq.consultation.resource.ListingResource;
import com.marcomnrq.consultation.resource.ProfessionalResource;
import com.marcomnrq.consultation.resource.SaveListingResource;
import com.marcomnrq.consultation.resource.SaveProfessionalResource;
import com.marcomnrq.consultation.service.ListingService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1")
public class ListingController {

    private final ListingService listingService;

    private final ModelMapper modelMapper;

    private final PrettyTime prettyTime;

    @PostMapping("listings")
    public ListingResource createListing(@RequestBody SaveListingResource resource, Principal principal){
        return convertToResource(listingService.createListing(principal.getName(), resource));
    }

    @GetMapping("listings")
    public Page<ListingResource> getAllListings(Pageable pageable){
        Page<Listing> listings = listingService.findAll(pageable);
        List<ListingResource> resources = listings.getContent().stream().map(this::convertToResource).collect(Collectors.toList());
        return new PageImpl<>(resources, pageable, resources.size());
    }

    @GetMapping("listings/search")
    public Page<ListingResource> searchListings(@RequestParam String title, Pageable pageable){
        // TODO: improve searching for listings
        Page<Listing> listings = listingService.searchListing(title, pageable);
        List<ListingResource> resources = listings.getContent().stream().map(this::convertToResource).collect(Collectors.toList());
        return new PageImpl<>(resources, pageable, resources.size());
    }

    @GetMapping("listings/{id}")
    public ListingResource getListingById(@PathVariable(name = "id") Long id){
        return convertToResource(listingService.getListingById(id));
    }

    @PutMapping("listings/{id}")
    public ListingResource editListingById(@PathVariable(name = "id") Long id, @RequestBody SaveListingResource resource, Principal principal){
        return convertToResource(listingService.editListing(principal.getName(), id, resource));
    }

    @DeleteMapping("listings/{id}")
    public ResponseEntity<?> deleteListingById(@PathVariable(name = "id") Long id, Principal principal){
        return listingService.deleteListing(principal.getName(), id);
    }


    private Listing convertToEntity(SaveListingResource resource) {
        return modelMapper.map(resource, Listing.class);
    }

    private ListingResource convertToResource(Listing entity) {
        ListingResource listing = (modelMapper.map(entity, ListingResource.class));
        listing.setCreated(prettyTime.format(listing.getCreatedAt()));
        listing.setUpdated(prettyTime.format(listing.getUpdatedAt()));
        return listing;
    }

}
