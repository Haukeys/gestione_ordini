package it.itsacademy.gestione_ordini.mapper;


import it.itsacademy.gestione_ordini.dto.OrdineCreateDTO;
import it.itsacademy.gestione_ordini.dto.OrdineDTO;
import it.itsacademy.gestione_ordini.dto.OrdineResponseDTO;
import it.itsacademy.gestione_ordini.entity.Ordine;
import it.itsacademy.gestione_ordini.entity.TipoOrdine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",imports = {TipoOrdine.class})
public interface OrdineMapper {

    @Mapping(target = "idOrdine", ignore = true)//ce sont, dans ce cas c'est, un des parametre a ignorer lord de la creation car il sont gere sans avoir a le faire manuellement
    @Mapping(target= "statoOrdine",ignore = true)
    Ordine toOrdine(OrdineCreateDTO ordineDTO);

    OrdineDTO toOrdineDTO(Ordine ordine);

    OrdineResponseDTO toOrdineResponseDTO(Ordine ordineResponse);
}
