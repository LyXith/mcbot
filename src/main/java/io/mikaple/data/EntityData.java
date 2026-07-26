package io.mikaple.data;

import org.cloudburstmc.math.vector.Vector3d;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundAddEntityPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.ClientboundEntityPositionSyncPacket;

import java.util.ArrayList;
import java.util.List;

import static io.mikaple.Main.entities;

public class EntityData {
    public ClientboundAddEntityPacket entity;
    public EntityData(ClientboundAddEntityPacket entity) {
        this.entity = entity;
    }

    public void updateEntity(ClientboundEntityPositionSyncPacket updatedEntity,Integer entityId) {
        if (this.entity.getEntityId() == entityId) {
            entities.remove(this);
            Vector3d updatedPositon = updatedEntity.getPosition();
            this.entity = new ClientboundAddEntityPacket(
                    updatedEntity.getId(),
                    this.entity.getUuid(),
                    this.entity.getType(),
                    updatedPositon.getX(),
                    updatedPositon.getY(),
                    updatedPositon.getZ(),
                    updatedEntity.getYRot(),
                    updatedEntity.getXRot(),
                    updatedEntity.getYRot()
            );
            entities.add(this);
        }
    }

    public static void updateEntity(ClientboundEntityPositionSyncPacket updatedEntity) {
        for (EntityData entityData : entities) {
            if (entityData.entity.getEntityId() == updatedEntity.getId()) {
                entityData.updateEntity(updatedEntity, updatedEntity.getId());
            }
        }
    }

    public static void removeEntity(int entityId) {
        entities.removeIf(entityData -> entityData.entity.getEntityId() == entityId);
    }

    public static List<EntityData> getPlayers() {
        List<EntityData> players = new ArrayList<>();
        for (EntityData entityData : entities) {
            if (entityData.entity.getType().equals(EntityType.PLAYER)) players.add(entityData);
        }
        return players;
    }
}
