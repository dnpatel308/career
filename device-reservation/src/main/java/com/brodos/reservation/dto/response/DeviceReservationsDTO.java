package com.brodos.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedModel;

@JsonInclude(Include.NON_NULL)
public class DeviceReservationsDTO {

    private ReservationDTO _embedded;
    @JsonIgnore
    private JsonNode links;
    @JsonIgnore
    private Links pageLinks;
    @JsonIgnore
    private PagedModel.PageMetadata page;

    @JsonProperty("_embedded")
    public ReservationDTO getEmbedded() {
        return _embedded;
    }

    public void setEmbedded(ReservationDTO _embedded) {
        this._embedded = _embedded;
    }

    @JsonIgnore
    @JsonProperty("_links")
    public JsonNode getLinks() {
        return links;
    }

    public PagedModel.PageMetadata getPage() {
        return page;
    }

    public void setLinksAndPage(PagedModel pagedModel) {
        pageLinks = pagedModel.getLinks();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode linksNode = new ObjectNode(JsonNodeFactory.instance);
        for (Link link : pageLinks) {
            // String href = removeParamFromUrl(link.getHref(), "_page");
            // href = removeParamFromUrl(href, "_size");
            // href = replaceParamFromUrl(href);
            String href = link.getHref();
            linksNode.put(link.getRel().value(), objectMapper.convertValue(new LinkDTO(href), JsonNode.class));
        }
        this.links = linksNode;
        this.page = pagedModel.getMetadata();
    }

    public String removeParamFromUrl(final String url, final String param) {
        if (StringUtils.isNotBlank(url)) {
            return url.replaceAll("&" + param + "=[^&]+", "").replaceAll("\\?" + param + "=[^&]+&", "?")
                .replaceAll("\\?" + param + "=[^&]+", "");
        } else {
            return url;
        }
    }

    public String replaceParamFromUrl(final String url) {
        if (StringUtils.isNotBlank(url)) {
            return url.replace("page", "_page").replace("size", "_size");
        } else {
            return url;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
