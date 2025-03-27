package com.monocept.chatbot.service.impl;

import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.entity.PlaceHolder;
import com.monocept.chatbot.model.request.OptionPlaceholderRequest;
import com.monocept.chatbot.reposiotry.PlaceholderRepository;
import com.monocept.chatbot.service.PlaceholderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class PlaceHolderServiceImpl implements PlaceholderService {

    private  final  PlaceholderRepository placeholderRepository;

    public PlaceHolderServiceImpl(PlaceholderRepository placeholderRepository) {
        this.placeholderRepository = placeholderRepository;
    }

    @Override
    public List<String> getAllPlaceholders() {
        return  placeholderRepository.findPlaceholderNames();
    }

    @Override
    public List<PlaceHolder> addPlaceholders(List<String> options) {
        List<PlaceHolder> list = options.stream().map(s -> PlaceHolder.builder().name(s).build()).toList();
        return placeholderRepository.saveAll(list);
    }

    @Override
    public String updatePlaceholder(String name) {
        return placeholderRepository.deleteByPlaceholderName(name) == 0
                ? "No matching record found to update."
                : "Record  updated.";
    }

    @Override
    public String deletePlaceholder(String name) {
        return placeholderRepository.deleteByPlaceholderName(name) == 0
                ? "No matching record found to update."
                : "Record  deleted.";
    }

    @Override
    public Object optionDataHandler(OptionPlaceholderRequest request)  {
        return switch (request.getMethodType().name().toLowerCase()) {
            case "add" -> this.addPlaceholders(request.getName());
            case "update" -> processOptions(request.getName(), this::updatePlaceholder);
            case "delete" -> processOptions(request.getName(), this::deletePlaceholder);
            default -> throw  new NoSuchElementException("Invalid option perform");
        };
    }

    private Map<String, String> processOptions(List<String> options, Function<String, String> action) {
        return options.stream().collect(Collectors.toMap(option -> option, action));
    }

}
