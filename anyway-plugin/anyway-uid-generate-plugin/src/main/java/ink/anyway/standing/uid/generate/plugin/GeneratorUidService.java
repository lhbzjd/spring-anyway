package ink.anyway.standing.uid.generate.plugin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "generator-uid-server")
public interface GeneratorUidService {

    @GetMapping(value = "/uid/generate")
    public long getUid();

    @GetMapping(value = "/uid/generate/batch/{size}")
    public List<Long> getBatchUid(@PathVariable(value = "size") int size);

}
