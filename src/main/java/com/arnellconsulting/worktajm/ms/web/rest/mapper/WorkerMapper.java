package com.arnellconsulting.worktajm.ms.web.rest.mapper;

import com.arnellconsulting.worktajm.ms.domain.*;
import com.arnellconsulting.worktajm.ms.web.rest.dto.WorkerDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Worker and its DTO WorkerDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface WorkerMapper {

    @Mapping(source = "workLog.id", target = "workLogId")
    WorkerDTO workerToWorkerDTO(Worker worker);

    List<WorkerDTO> workersToWorkerDTOs(List<Worker> workers);

    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "organizations", ignore = true)
    @Mapping(source = "workLogId", target = "workLog")
    Worker workerDTOToWorker(WorkerDTO workerDTO);

    List<Worker> workerDTOsToWorkers(List<WorkerDTO> workerDTOs);

    default WorkLog workLogFromId(Long id) {
        if (id == null) {
            return null;
        }
        WorkLog workLog = new WorkLog();
        workLog.setId(id);
        return workLog;
    }
}
