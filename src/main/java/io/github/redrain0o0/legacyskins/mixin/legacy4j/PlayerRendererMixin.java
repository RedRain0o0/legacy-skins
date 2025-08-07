//? if figurac && >=1.21.2 {
package io.github.redrain0o0.legacyskins.mixin.legacy4j;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
//? if >=1.21.2 {
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
 //?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.FactoryAPIClient;

import java.util.Arrays;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer {

	public PlayerRendererMixin(EntityRendererProvider.Context context, EntityModel entityModel, float f) {
		super(context, entityModel, f);
	}

	//? if <1.21.2 {
	/*@Redirect(method = "renderHand", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/PlayerModel;swimAmount:F", opcode = Opcodes.PUTFIELD))
	private void renderHand(PlayerModel instance, float value, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer abstractClientPlayer) {
		instance.swimAmount = abstractClientPlayer.getSwimAmount(FactoryAPIClient.getGamePartialTick(false));
		((PlayerModel)getModel()).rightArmPose = HumanoidModel.ArmPose.EMPTY;
		((PlayerModel)getModel()).leftArmPose = HumanoidModel.ArmPose.EMPTY;
	}
	@Redirect(method = /^? if <1.20.5 {^//^"setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V"^//^?} else {^/"setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V"/^?}^/, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isFallFlying()Z"))
	private boolean render(AbstractClientPlayer instance) {
		return instance.isFallFlying() && instance.getPose() == Pose.FALL_FLYING;
	}
	*///?} else {
    @Shadow
	public abstract PlayerRenderState createRenderState();

	// Harmonize Legacy4J's mixin with Figura's mixin
    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void renderHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ResourceLocation resourceLocation, ModelPart modelPart, boolean bl, CallbackInfo ci) {
        PlayerRenderState state = createRenderState();
		if (!(getModel() instanceof PlayerModel model)) return;
		ModelPart[] parts = new ModelPart[]{
				model.head,
				model.hat,
				model.rightArm,
				model.body,
				model.leftArm,
				model.rightLeg,
				model.leftLeg,
				model.leftSleeve,
				model.rightSleeve,
				model.leftPants,
				model.rightPants,
				model.jacket
		};
		Boolean[] visibilities = Arrays.stream(parts).map(a -> a.visible).toArray(Boolean[]::new);
        state.swimAmount = Minecraft.getInstance().player.getSwimAmount(FactoryAPIClient.getDeltaTracker().getGameTimeDeltaPartialTick(true));
        getModel().setupAnim(state);
		for (int i1 = 0; i1 < parts.length; i1++) {
			parts[i1].visible = visibilities[i1];
		}
    }
    @Redirect(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;isFallFlying:Z"))
    private boolean render(PlayerRenderState instance) {
        return instance.isFallFlying && instance.hasPose(Pose.FALL_FLYING);
    }
    //?}
}
//?} else {
/*package io.github.redrain0o0.legacyskins.mixin.legacy4j;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class PlayerRendererMixin {

}
*///?}