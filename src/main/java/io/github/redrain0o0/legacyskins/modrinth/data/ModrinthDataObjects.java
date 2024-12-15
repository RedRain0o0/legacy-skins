package io.github.redrain0o0.legacyskins.modrinth.data;

import com.google.common.hash.Hashing;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class ModrinthDataObjects {


	public record ProjectId(String str) {
		public static final Codec<ProjectId> CODEC = Codec.STRING.xmap(ProjectId::new, ProjectId::str);
	}
	public record CollectionId(String str) {
		public static final Codec<CollectionId> CODEC = Codec.STRING.xmap(CollectionId::new, CollectionId::str);
	}
	public record UserId(String str) {
		public static final Codec<UserId> CODEC = Codec.STRING.xmap(UserId::new, UserId::str);
	}
	public record OrganizationId(String str) {
		public static final Codec<OrganizationId> CODEC = Codec.STRING.xmap(OrganizationId::new, OrganizationId::str);
	}
	public record VersionId(String str) {
		public static final Codec<VersionId> CODEC = Codec.STRING.xmap(VersionId::new, VersionId::str);
	}
	public record TeamId(String str) {
		public static final Codec<TeamId> CODEC = Codec.STRING.xmap(TeamId::new, TeamId::str);
	}

	// Note, v3 API
	public record Collection(CollectionId id,
							 UserId userId,
							 String name,
							 Optional<String> description,
							 Instant created,
							 Instant updated,
							 Optional<String> iconUrl,
							 Optional<String> rawIconUrl,
							 Optional<Color> color,
							 CollectionStatus status,
							 List<ProjectId> projects) {
		public static final Codec<Collection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				CollectionId.CODEC.fieldOf("id").forGetter(Collection::id),
				UserId.CODEC.fieldOf("user").forGetter(Collection::userId),
				Codec.STRING.fieldOf("name").forGetter(Collection::name),
				Codec.STRING.optionalFieldOf("description").forGetter(Collection::description),
				JavaCodecs.INSTANT.fieldOf("created").forGetter(Collection::created),
				JavaCodecs.INSTANT.fieldOf("updated").forGetter(Collection::updated),
				Codec.STRING.optionalFieldOf("icon_url").forGetter(Collection::iconUrl),
				Codec.STRING.optionalFieldOf("raw_icon_url").forGetter(Collection::rawIconUrl),
				JavaCodecs.COLOR.optionalFieldOf("color").forGetter(Collection::color),
				CollectionStatus.CODEC.fieldOf("status").forGetter(Collection::status),
				ProjectId.CODEC.listOf().fieldOf("projects").forGetter(Collection::projects)
		).apply(instance, Collection::new));
	}

	public enum CollectionStatus {
		LISTED,
		UNLISTED,
		PRIVATE,
		// how do you reject a collection
		REJECTED,
		UNKNOWN;

		public static final Codec<CollectionStatus> CODEC = Codec.STRING.xmap(CollectionStatus::fromString, CollectionStatus::toString);

		private static CollectionStatus fromString(String string) {
			return switch (string) {
				case "listed" -> LISTED;
				case "unlisted" -> UNLISTED;
				case "private" -> PRIVATE;
				case "rejected" -> REJECTED;
				default -> UNKNOWN;
			};
		}

		@Override
		public String toString() {
			return switch (this) {
				case LISTED -> "listed";
				case UNLISTED -> "unlisted";
				case PRIVATE -> "private";
				case REJECTED -> "rejected";
				case UNKNOWN -> "unknown";
			};
		}
	}

	// Note, not everything, just what is needed for Legacy Skins, also v3 API
	public record Project(
			ProjectId id,
			Optional<String> slug,
			List<String> projectTypes, // should contain resource packs
			Optional<OrganizationId> organization,
			TeamId teamId,
			String name,
			String summary,
			// String description, // It is not worth the file size to be parsing markdown.
			int downloads,
			List<VersionId> versions
	) {
		public static final Codec<Project> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ProjectId.CODEC.fieldOf("id").forGetter(Project::id),
				Codec.STRING.optionalFieldOf("slug").forGetter(Project::slug),
				Codec.STRING.listOf().fieldOf("project_types").forGetter(Project::projectTypes),
				OrganizationId.CODEC.optionalFieldOf("organization").forGetter(Project::organization),
				TeamId.CODEC.fieldOf("team_id").forGetter(Project::teamId),
				Codec.STRING.fieldOf("name").forGetter(Project::name),
				Codec.STRING.fieldOf("summary").forGetter(Project::summary),
				Codec.INT.fieldOf("downloads").forGetter(Project::downloads),
				VersionId.CODEC.listOf().fieldOf("versions").forGetter(Project::versions)
		).apply(instance, Project::new));
	}

	// This is all the info we need in Legacy Skins
	public record User(
			UserId id,
			String username
	) {
		public static final Codec<User> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				UserId.CODEC.fieldOf("id").forGetter(User::id),
				Codec.STRING.fieldOf("username").forGetter(User::username)
		).apply(instance, User::new));
	}
	public record TeamMember(
			TeamId teamId,
			User user,
			String role,
			boolean isOwner
	) {
		public static final Codec<TeamMember> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				TeamId.CODEC.fieldOf("team_id").forGetter(TeamMember::teamId),
				User.CODEC.fieldOf("user").forGetter(TeamMember::user),
				Codec.STRING.fieldOf("role").forGetter(TeamMember::role),
				Codec.BOOL.fieldOf("is_owner").forGetter(TeamMember::isOwner)
		).apply(instance, TeamMember::new));
	}

	public record Organization(
			OrganizationId id,
			String slug,
			String name
	) {
		public static final Codec<Organization> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				OrganizationId.CODEC.fieldOf("id").forGetter(Organization::id),
				Codec.STRING.fieldOf("slug").forGetter(Organization::slug),
				Codec.STRING.fieldOf("name").forGetter(Organization::name)
		).apply(instance, Organization::new));
	}

	public record Version(
			VersionId id,
			ProjectId projectId,
			String versionNumber,
			List<String> projectTypes,
			List<String> gameVersions, // note that we ignore this.
			// VersionType versionType, // TODO
			Instant datePublished,
			List<VersionFile> files
	) {
		public static final Codec<Version> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				VersionId.CODEC.fieldOf("id").forGetter(Version::id),
				ProjectId.CODEC.fieldOf("project_id").forGetter(Version::projectId),
				Codec.STRING.fieldOf("version_number").forGetter(Version::versionNumber),
				Codec.STRING.listOf().fieldOf("project_types").forGetter(Version::projectTypes),
				Codec.STRING.listOf().fieldOf("game_versions").forGetter(Version::gameVersions),
				JavaCodecs.INSTANT.fieldOf("date_published").forGetter(Version::datePublished),
				VersionFile.CODEC.listOf().fieldOf("files").forGetter(Version::files)
		).apply(instance, Version::new));
	}

	public record OauthTokenPostDto(
			String code,
			String clientId,
			String redirectUri,
			String grantType
	) {
		public static final Codec<OauthTokenPostDto> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("code").forGetter(OauthTokenPostDto::code),
				Codec.STRING.fieldOf("client_id").forGetter(OauthTokenPostDto::clientId),
				Codec.STRING.fieldOf("redirect_uri").forGetter(OauthTokenPostDto::redirectUri),
				Codec.STRING.fieldOf("grant_type").forGetter(OauthTokenPostDto::grantType)
		).apply(instance, OauthTokenPostDto::new));
	}

	public record OauthTokenResponse(
			String accessToken,
			String tokenType,
			// it can't be more than 2 billion seconds, right?
			int expiresIn
	) {
		public static final Codec<OauthTokenResponse> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("access_token").forGetter(OauthTokenResponse::accessToken),
				Codec.STRING.fieldOf("token_type").forGetter(OauthTokenResponse::tokenType),
				Codec.INT.fieldOf("expires_in").forGetter(OauthTokenResponse::expiresIn)
		).apply(instance, OauthTokenResponse::new));
	}

	public record VersionFile(
			Hashes hashes,
			String url,
			String filename,
			boolean primary,
			int size, // assume that we don't get a file larger than 2 GB
			Optional<String> fileType
	) {
		public static final Codec<VersionFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Hashes.CODEC.fieldOf("hashes").forGetter(VersionFile::hashes),
				Codec.STRING.fieldOf("url").forGetter(VersionFile::url),
				Codec.STRING.fieldOf("filename").forGetter(VersionFile::filename),
				Codec.BOOL.fieldOf("primary").forGetter(VersionFile::primary),
				Codec.INT.fieldOf("size").forGetter(VersionFile::size),
				Codec.STRING.optionalFieldOf("file_type").forGetter(VersionFile::fileType)
		).apply(instance, VersionFile::new));
	}
	@SuppressWarnings("deprecation")
	public record Hashes(byte[] sha512, byte[] sha1) {
		public static final Codec<Hashes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.xmap(a -> Hashing.sha512().hashBytes(a.getBytes(StandardCharsets.UTF_8)).asBytes(), a -> Hashing.sha512().hashBytes(a).toString()).fieldOf("sha512").forGetter(Hashes::sha512),
				Codec.STRING.xmap(a -> Hashing.sha1().hashBytes(a.getBytes(StandardCharsets.UTF_8)).asBytes(), a -> Hashing.sha1().hashBytes(a).toString()).fieldOf("sha1").forGetter(Hashes::sha1)
		).apply(instance, Hashes::new));
	}
}
