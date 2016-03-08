package net.rperce.compactstuff;

import java.util.stream.Stream;

public class ClientOnlyProxy extends CommonProxy {
    @Override
    protected Stream<BaseStartup> toInit() {
        Stream.Builder<BaseStartup> out = Stream.builder();
        out.add(new net.rperce.compactstuff.blockcompact.StartupClientOnly());
        out.add(new net.rperce.compactstuff.comglass.StartupClientOnly());
        out.add(new net.rperce.compactstuff.compactor.StartupClientOnly());
        return Stream.concat(super.toInit(), out.build());
    }
}
