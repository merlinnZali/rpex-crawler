package ttm.eu;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Builder
@Data
public class Link {
    private String url;
    private Integer depth;
    private Integer status;
    private boolean discovered;
}
